package com.example.roblocks.domain.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.ai.ImageClass
import com.example.roblocks.ai.ImageClassifier
import com.example.roblocks.ai.ImageUploader
import com.example.roblocks.ai.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import retrofit2.HttpException

@HiltViewModel
class ClassifierViewModel @Inject constructor(
    private val imageUploader: ImageUploader,
    private val imageClassifier: ImageClassifier
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClassifierUiState())
    val uiState: StateFlow<ClassifierUiState> = _uiState.asStateFlow()

    private val _imageClasses = MutableStateFlow(listOf(ImageClass("Class 1"), ImageClass("Class 2")))
    val imageClasses: StateFlow<List<ImageClass>> = _imageClasses.asStateFlow()

    fun addNewClass() {
        val newClassName = "Class ${_imageClasses.value.size + 1}"
        _imageClasses.value = _imageClasses.value + ImageClass(newClassName)
    }

    fun addImageToClass(classIndex: Int, bitmap: Bitmap) {
        val currentList = _imageClasses.value.toMutableList()
        if (classIndex < currentList.size) {
            currentList[classIndex] = currentList[classIndex].copy(
                imageCount = currentList[classIndex].imageCount + 1,
                images = currentList[classIndex].images + bitmap
            )
            _imageClasses.value = currentList.toList()
            val className = currentList[classIndex].name
            if (className == "Class 1") {
                _uiState.update { it.copy(class1Images = it.class1Images + bitmap) }
            } else if (className == "Class 2") {
                _uiState.update { it.copy(class2Images = it.class2Images + bitmap) }
            }
        }
    }

    fun addClass1Image(bitmap: Bitmap) {
        _uiState.update { it.copy(class1Images = it.class1Images + bitmap) }
        addImageToClass(0, bitmap)
    }

    fun addClass2Image(bitmap: Bitmap) {
        _uiState.update { it.copy(class2Images = it.class2Images + bitmap) }
        addImageToClass(1, bitmap)
    }

    fun setSelectedClass(cls: Int) {
        _uiState.update { it.copy(selectedClass = cls) }
    }

    fun setPredictionResult(result: String, confidence: Float) {
        _uiState.update { it.copy(predictionResult = result, confidence = confidence) }
    }

    fun setTrainingError(message: String) {
        _uiState.update { it.copy(trainingError = message) }
    }

    fun updateEpochs(epochs: Int) {
        _uiState.update { it.copy(epochs = epochs.coerceIn(1, 100)) }
    }

    fun updateBatchSize(batchSize: Int) {
        _uiState.update { it.copy(batchSize = batchSize.coerceIn(1, 64)) }
    }

    fun updateLearningRate(learningRate: Float) {
        _uiState.update { it.copy(learningRate = learningRate.coerceIn(0.001f, 0.1f)) }
    }

    fun trainModel(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTraining = true, trainingError = null, trainingMessage = "Uploading images...") }
            val allImages = _imageClasses.value.flatMap { it.images }
            val classLabels = _imageClasses.value.map { it.name }
            val imagesPerClass = _imageClasses.value.map { it.images.size }
            try {
                val response = imageUploader.uploadImages(
                    allImages, classLabels, imagesPerClass,
                    uiState.value.epochs, uiState.value.batchSize, uiState.value.learningRate
                )
                Log.d("ClassifierViewModel", "Backend response: $response")
                if (response != null && response["status"] == "success") {
                    _uiState.update { it.copy(trainingMessage = "Downloading model...") }
                    val modelFile = withContext(Dispatchers.IO) { imageUploader.downloadModel() }
                    if (modelFile != null) {
                        imageClassifier.loadModel(modelFile)
                        imageClassifier.setClassLabels(classLabels)
                        val metrics = response["metrics"] as? Map<*, *>
                        val trainingMetrics: Map<String, List<Float>>? = metrics?.let {
                            try {
                                val epochsList: List<Float> = (it["epochs"] as? List<*>)?.mapNotNull { num ->
                                    (num as? Number)?.toFloat()
                                } ?: emptyList()
                                val mAP50List: List<Float> = (it["mAP50"] as? List<*>)?.mapNotNull { num ->
                                    (num as? Number)?.toFloat()
                                } ?: emptyList()
                                mapOf(
                                    "epochs" to epochsList,
                                    "mAP50" to mAP50List
                                )
                            } catch (e: Exception) {
                                Log.e("ClassifierViewModel", "Failed to parse metrics: ${e.message}")
                                null
                            }
                        }
                        _uiState.update {
                            it.copy(
                                isTraining = false,
                                modelTrained = true,
                                trainingMessage = "Model trained successfully",
                                trainingMetrics = trainingMetrics
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isTraining = false, trainingError = "Failed to download model") }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isTraining = false,
                            trainingError = response?.get("error")?.toString() ?: "Failed to upload images"
                        )
                    }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _uiState.update {
                    it.copy(
                        isTraining = false,
                        trainingError = when (e.code()) {
                            400 -> "Invalid request: $errorBody"
                            404 -> "Model not found"
                            else -> "Network error: ${e.message}"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isTraining = false, trainingError = "Training failed: ${e.message}") }
            }
        }
    }

    fun classifyImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                val (label, confidence) = imageClassifier.classify(bitmap)
                setPredictionResult(label, confidence)
            } catch (e: Exception) {
                setTrainingError("Classification error: ${e.message}")
            }
        }
    }

    fun hasStoragePermissions(context: Context): Boolean {
        return PermissionUtils.hasStoragePermissions(context)
    }

    fun requestStoragePermission(context: Context) {
        _uiState.update { it.copy(showPermissionDialog = true) }
    }

    fun dismissPermissionDialog() {
        _uiState.update { it.copy(showPermissionDialog = false) }
    }

    fun resetPermissionRequest() {
        _uiState.update { it.copy(shouldRequestStoragePermission = false) }
    }

    override fun onCleared() {
        imageClassifier.close()
        super.onCleared()
    }

    data class ClassifierUiState(
        val class1Images: List<Bitmap> = emptyList(),
        val class2Images: List<Bitmap> = emptyList(),
        val isTraining: Boolean = false,
        val modelTrained: Boolean = false,
        val trainingMessage: String? = null,
        val trainingError: String? = null,
        val predictionResult: String? = null,
        val listOfPredictionResult: List<String> = emptyList(),
        val confidence: Float = 0f,
        val showPermissionDialog: Boolean = false,
        val shouldRequestStoragePermission: Boolean = false,
        val selectedClass: Int = 1,
        val epochs: Int = 10,
        val batchSize: Int = 32,
        val learningRate: Float = 0.001f,
        val trainingMetrics: Map<String, List<Float>>? = null
    )
}