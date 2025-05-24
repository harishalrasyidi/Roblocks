package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.roblocks.data.remote.BackendApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import javax.inject.Inject

class ImageUploader @Inject constructor(
    private val context: Context,
    private val apiService: BackendApiService
) {
    suspend fun uploadImages(
        images: List<Bitmap>,
        classLabels: List<String>,
        imagesPerClass: List<Int>,
        epochs: Int,
        batchSize: Int,
        learningRate: Float
    ): Map<String, Any>? = withContext(Dispatchers.IO) {
        if (images.isEmpty() || classLabels.isEmpty() || imagesPerClass.isEmpty()) {
            Log.e("Uploader", "Empty input: images=${images.size}, labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
            return@withContext null
        }
        if (classLabels.size != imagesPerClass.size) {
            Log.e("Uploader", "Mismatch: labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
            return@withContext null
        }
        if (imagesPerClass.sum() != images.size) {
            Log.e("Uploader", "Total images (${images.size}) does not match sum of imagesPerClass (${imagesPerClass.sum()})")
            return@withContext null
        }
        try {
            val imageParts = mutableListOf<MultipartBody.Part>()
            var imageIndex = 0
            classLabels.forEachIndexed { classIndex, label ->
                val numImages = imagesPerClass[classIndex]
                for (i in 0 until numImages) {
                    val bitmap = images[imageIndex]
                    val file = createTempFile(bitmap, "${label}_img_$i.jpg")
                    val requestBody = file.readBytes().toRequestBody("image/jpeg".toMediaType())
                    val part = MultipartBody.Part.createFormData(
                        "images",
                        file.name,
                        requestBody
                    )
                    imageParts.add(part)
                    imageIndex++
                }
            }
            val response = apiService.uploadImages(imageParts, classLabels, epochs, batchSize, learningRate)
            Log.d("Uploader", "Upload response: $response")
            response
        } catch (e: Exception) {
            Log.e("Uploader", "Upload failed", e)
            null
        }
    }

    suspend fun downloadModel(): File? = withContext(Dispatchers.IO) {
        try {
            val responseBody = apiService.downloadModel()
            val modelFile = File(context.filesDir, "model.tflite")
            responseBody.byteStream().use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("Uploader", "Model downloaded to ${modelFile.absolutePath}")
            modelFile
        } catch (e: Exception) {
            Log.e("Uploader", "Download failed", e)
            null
        }
    }

    private fun createTempFile(bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return file
    }
}