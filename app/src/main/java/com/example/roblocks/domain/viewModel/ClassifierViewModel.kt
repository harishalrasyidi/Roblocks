package com.example.roblocks.domain.viewModel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.ai.ImageClass
import com.example.roblocks.ai.ImageClassifier
import com.example.roblocks.ai.ImageUploader
import com.example.roblocks.ai.ModelTrainer
import com.example.roblocks.ai.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class ClassifierViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClassifierUiState())
    val uiState: StateFlow<ClassifierUiState> = _uiState

    private var classifier: ImageClassifier? = null

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
        _uiState.value = _uiState.value.copy(
            class1Images = _uiState.value.class1Images + bitmap
        )
    }

    fun addClass2Image(bitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(
            class2Images = _uiState.value.class2Images + bitmap
        )
    }

    fun setSelectedClass(cls: Int) {
        _uiState.value = _uiState.value.copy(selectedClass = cls)
    }

    fun setPredictionResult(result: String, confidence: Float) {
        _uiState.value = _uiState.value.copy(predictionResult = result, confidence = confidence)
    }

    fun setTrainingError(message: String) {
        _uiState.value = _uiState.value.copy(trainingError = message)
    }

    fun trainModel(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTraining = true, trainingError = null, trainingMessage = "Uploading images...") }

            val uploader = ImageUploader()
            // Collect all images from all classes
            val allImages = _imageClasses.value.flatMap { it.images }
            val classLabels = _imageClasses.value.map { it.name }
            val imagesPerClass = _imageClasses.value.map { it.images.size }


            val uploadSuccess = uploader.uploadImages(context, allImages, classLabels, imagesPerClass)

            if (uploadSuccess) {
                _uiState.update { it.copy(trainingMessage = "Training on server...") }

                try {
                    val downloadUrl = "http://192.168.1.11:5000/download-model"
                    val file = withContext(Dispatchers.IO) {
                        val url = URL(downloadUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.connect()

                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            val inputStream = connection.inputStream
                            val modelFile = File(context.filesDir, "color_classifier.tflite")
                            val outputStream = FileOutputStream(modelFile)
                            inputStream.copyTo(outputStream)
                            outputStream.close()
                            inputStream.close()
                            modelFile
                        } else null
                    }

                    if (file != null) {
                        if (classifier == null) {
                            classifier = ImageClassifier(context)
                        }
                        classifier?.loadModel(file)
                        // Train with all classes' images
                        classifier?.setTrainingData(_imageClasses.value.map { it.images }, classLabels)
                        _uiState.update {
                            it.copy(
                                isTraining = false,
                                modelTrained = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isTraining = false,
                                trainingError = "Failed to download model"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isTraining = false,
                            trainingError = "Training failed: ${e.message}"
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isTraining = false,
                        trainingError = "Failed to upload images"
                    )
                }
            }
        }
    }




    fun classifyImage(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                if (classifier == null) {
                    classifier = ImageClassifier(context)
                    // model sudah harus di-load sebelumnya saat training
                }
                val (label, confidence) = classifier?.classify(bitmap) ?: Pair("Unknown", 0f)
                setPredictionResult(label, confidence)
            } catch (e: Exception) {
                setTrainingError("Classification error: ${e.message}")
            }
        }
    }

    fun downloadTrainedModel(
        context: Context,
        url: String,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val trainer = ModelTrainer(context)
                val file = withContext(kotlinx.coroutines.Dispatchers.IO) {
                    trainer.downloadModel(url)
                }
                classifier = ImageClassifier(context)
                classifier?.loadModel(file)
                _uiState.value = _uiState.value.copy(modelTrained = true)
                onSuccess(file)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }


    // --- Permission Helpers (opsional) ---
    fun hasStoragePermissions(context: Context): Boolean {
        return PermissionUtils.hasStoragePermissions(context)
    }

    fun requestStoragePermission(context: Context) {
        _uiState.value = _uiState.value.copy(showPermissionDialog = true)
    }

    fun dismissPermissionDialog() {
        _uiState.value = _uiState.value.copy(showPermissionDialog = false)
    }

    fun resetPermissionRequest() {
        _uiState.value = _uiState.value.copy(shouldRequestStoragePermission = false)
    }

    data class ClassifierUiState(
        val class1Images: List<Bitmap> = emptyList(),
        val class2Images: List<Bitmap> = emptyList(),
        val isTraining: Boolean = false,
        val modelTrained: Boolean = false,
        val trainingMessage: String? = null,
        val trainingError: String? = null,
        val predictionResult: String? = null,
        val confidence: Float = 0f,
        val showPermissionDialog: Boolean = false,
        val shouldRequestStoragePermission: Boolean = false,
        val selectedClass: Int = 1,
        val epochs: Int = 10,
        val batchSize: Int = 32,
        val learningRate: Float = 0.001f
    )
}