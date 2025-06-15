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
import com.example.roblocks.ai.TrainingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.Result

@HiltViewModel
class ClassifierViewModel @Inject constructor(
    private val imageUploader: ImageUploader,
    private val imageClassifier: ImageClassifier
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClassifierUiState())
    val uiState: StateFlow<ClassifierUiState> = _uiState.asStateFlow()

    private val _imageClasses = MutableStateFlow(listOf(ImageClass("Class_1"), ImageClass("Class_2")))
    val imageClasses: StateFlow<List<ImageClass>> = _imageClasses.asStateFlow()

    fun addNewClass() {
        val newClassName = "Class_${_imageClasses.value.size + 1}"
        _imageClasses.value += ImageClass(newClassName)
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
            if (className == "Class_1") {
                _uiState.update { it.copy(class1Images = it.class1Images + bitmap) }
            } else if (className == "Class_2") {
                _uiState.update { it.copy(class2Images = it.class2Images + bitmap) }
            }
        }
    }

    fun changeClassName(classIndex: Int, newName: String) {
        if (classIndex < _imageClasses.value.size) {
            val updatedClasses = _imageClasses.value.toMutableList()
            updatedClasses[classIndex] = updatedClasses[classIndex].copy(name = newName)
            _imageClasses.value = updatedClasses.toList()
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

    fun setTrainingError(message: String?) {
        _uiState.update { it.copy(trainingError = message) }
    }

    fun updateEpochs(epochs: Int) {
        _uiState.update { it.copy(epochs = epochs.coerceIn(1, 100)) }
    }

    fun updateBatchSize(batchSize: Int) {
        _uiState.update { it.copy(batchSize = batchSize.coerceIn(1, 256)) }
    }

    fun updateLearningRate(learningRate: Float) {
        _uiState.update { it.copy(learningRate = learningRate.coerceIn(0.001f, 0.01f)) }
    }

    private fun validateAllClasses() {
        val minImages = 3 // Match ImageUploader.MIN_IMAGES_PER_CLASS
        val invalidClass = _imageClasses.value.firstOrNull { it.images.size < minImages }
        if (invalidClass != null) {
            setTrainingError("Not enough images in ${invalidClass.name}. Minimum $minImages required.")
        } else {
            setTrainingError(null)
        }
    }

    fun trainModel(context: Context) {
        validateAllClasses()
        if (_uiState.value.trainingError != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isTraining = true, trainingError = null, trainingMessage = "Uploading images...") }
            val allImages = _imageClasses.value.flatMap { it.images }
            val classLabels = _imageClasses.value.map { it.name }
            val imagesPerClass = _imageClasses.value.map { it.images.size }
            val epochs = _uiState.value.epochs ?: 20
            val batchSize = _uiState.value.batchSize ?: 16
            val learningRate = _uiState.value.learningRate ?: 0.001f

            val uploadResult = imageUploader.uploadImages(
                allImages, classLabels, imagesPerClass,
                epochs, batchSize, learningRate
            )
            if (uploadResult.isSuccess) {
                val response = uploadResult.getOrThrow()

                if (response.status == "success") {
                    _uiState.update { it.copy(trainingMessage = "Downloading model...") }

                    val modelResult = imageUploader.downloadModel(context = context, sessionId = response.session_id)

                    if (modelResult.isSuccess) {
                        val modelFile = modelResult.getOrThrow()
                        imageClassifier.loadModel(modelFile)
                        imageClassifier.setClassLabels(response.class_names_inferred)

                        val trainingMetrics = mapOf(
                            "accuracy" to response.metrics_history.accuracy,
                            "val_accuracy" to response.metrics_history.val_accuracy,
                            "loss" to response.metrics_history.loss,
                            "val_loss" to response.metrics_history.val_loss
                        )
                        _uiState.update {
                            it.copy(
                                isTraining = false,
                                modelTrained = true,
                                trainingMessage = "Model trained successfully (Validation Accuracy: ${response.final_val_accuracy})",
                                trainingMetrics = trainingMetrics,
                                trainingError = if (response.final_val_accuracy < 0.7f) {
                                    "Low validation accuracy. Consider adding more images."
                                } else null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isTraining = false,
                                trainingError = "Failed to download model: ${modelResult.exceptionOrNull()?.message ?: "Unknown error"}"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isTraining = false,
                            trainingError = "Training failed: ${response.status}"
                        )
                    }
                }
            } else {
                val errorMessage = when (val e = uploadResult.exceptionOrNull()) {
                    is HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                        when (e.code()) {
                            400 -> "Invalid request: $errorBody"
                            404 -> "Server endpoint not found"
                            else -> "Network error: ${e.message}"
                        }
                    }
                    else -> "Training failed: ${e?.message ?: "Unknown error"}"
                }
                _uiState.update {
                    it.copy(
                        isTraining = false,
                        trainingError = errorMessage
                    )
                }
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
        var class1Images: List<Bitmap> = emptyList(),
        var class2Images: List<Bitmap> = emptyList(),
        var isTraining: Boolean = false,
        var modelTrained: Boolean = false,
        var trainingMessage: String? = null,
        var trainingError: String? = null,
        var predictionResult: String? = null,
        var listOfPredictionResult: List<String> = emptyList(),
        var confidence: Float = 0f,
        var showPermissionDialog: Boolean = false,
        var shouldRequestStoragePermission: Boolean = false,
        var selectedClass: Int = 1,
        var epochs: Int? = null,
        var batchSize: Int? = null,
        var learningRate: Float? = null,
        var trainingMetrics: Map<String, List<Float>>? = null,
        var session_id: String = "session_test_debug"
    )
}