package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.roblocks.data.remote.BackendApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.roblocks.data.remote.BackendApiServiceClassifier
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


data class TrainingResponse(
    val status: String,
    val session_id: String,
    val model_path: String,
    val class_names_inferred: List<String>,
    val final_val_accuracy: Float,
    val metrics_history: MetricsHistory
)

data class MetricsHistory(
    val epochs: List<Int>,
    val loss: List<Float>,
    val accuracy: List<Float>,
    val val_loss: List<Float>,
    val val_accuracy: List<Float>
)



class ImageUploader @Inject constructor(
    private val context: Context,
    private val apiService: BackendApiServiceClassifier
) {
    private companion object {
        const val MIN_IMAGES_PER_CLASS = 3
        val sessionId = "session_test_debug"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Time to establish connection
        .readTimeout(60, TimeUnit.SECONDS)    // Time to read data from server
        .writeTimeout(60, TimeUnit.SECONDS)   // Time to send data to server
        .callTimeout(5, TimeUnit.MINUTES)      // Total time for the entire request
        .build()

    suspend fun uploadImages(
        images: List<Bitmap>,
        classLabels: List<String>,
        imagesPerClass: List<Int>,
        epochs: Int,
        batchSize: Int,
        learningRate: Float
    ): Result<TrainingResponse> = withContext(Dispatchers.IO) {
        try {
            // Input validation
            if (images.isEmpty() || classLabels.isEmpty() || imagesPerClass.isEmpty()) {
                Log.e("ImageUploader", "Empty input: images=${images.size}, labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
                return@withContext Result.failure(Exception("Empty input provided"))
            }
            if (classLabels.size != imagesPerClass.size) {
                Log.e("ImageUploader", "Mismatch: labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
                return@withContext Result.failure(Exception("Number of labels must match imagesPerClass"))
            }
            if (imagesPerClass.sum() != images.size) {
                Log.e("ImageUploader", "Total images (${images.size}) does not match sum of imagesPerClass (${imagesPerClass.sum()})")
                return@withContext Result.failure(Exception("Total images must match sum of imagesPerClass"))
            }
            if (imagesPerClass.any { it < MIN_IMAGES_PER_CLASS }) {
                Log.e("ImageUploader", "Each class must have at least $MIN_IMAGES_PER_CLASS images, got $imagesPerClass")
                return@withContext Result.failure(Exception("Each class must have at least $MIN_IMAGES_PER_CLASS images"))
            }
            if (learningRate > 0.01f) {
                Log.w("ImageUploader", "Learning rate $learningRate is high; consider using 0.001 or lower")
            }

            // Prepare image parts
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
            // Prepare class label parts (backend expects quoted strings)
            val classLabelParts = classLabels.map { label ->
                MultipartBody.Part.createFormData("class_label", "\"$label\"")
            }


            // Send request
            val response = apiService.uploadImages(
                sessionId = sessionId,
                images = imageParts,
                classLabelParts = classLabelParts,
                epochs = epochs,
                batchSize = batchSize,
                learningRate = learningRate
            )
            Log.d("ImageUploader", "Upload response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("ImageUploader", "Upload failed", e)
            Result.failure(e)
        }
    }



    suspend fun downloadModel(context: Context, sessionId: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Check network availability
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                Log.e("ImageUploader", "No network available")
                return@withContext Result.failure(IOException("No network available"))
            }

            // Validate sessionId
            if (sessionId.isBlank()) {
                Log.e("ImageUploader", "Invalid sessionId: $sessionId")
                return@withContext Result.failure(IllegalArgumentException("Invalid sessionId"))
            }

            Log.d("ImageUploader", "Downloading model with sessionId: $sessionId")
            val response = apiService.downloadModel(sessionId = sessionId)

            val responseBody = apiService.downloadModel(sessionId)

            if (responseBody == null) {
                Log.e("ImageUploader", "Response body is null")
                return@withContext Result.failure(IOException("Empty response body"))
            }

            val modelFile = File(context.filesDir, "model.tflite")
            responseBody.byteStream().use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (!modelFile.exists() || modelFile.length() == 0L) {
                Log.e("ImageUploader", "Model file not created or empty at ${modelFile.absolutePath}")
                return@withContext Result.failure(IOException("Failed to save model file"))
            }

            Log.d("ImageUploader", "Model downloaded and saved to ${modelFile.absolutePath}")
            Result.success(modelFile)

            // Verify file exists and has content
            if (!modelFile.exists() || modelFile.length() == 0L) {
                Log.e("ImageUploader", "Model file not created or empty at ${modelFile.absolutePath}")
                return@withContext Result.failure(IOException("Failed to save model file"))
            }

            Log.d("ImageUploader", "Model downloaded and saved to ${modelFile.absolutePath}")
            Result.success(modelFile)
        } catch (e: Exception) {
            Log.e("ImageUploader", "Download failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun createTempFile(bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        } catch (e: IOException) {
            Log.e("Uploader", "Failed to write bitmap to file: $fileName", e)
            throw e
        }
        return file
    }
}