package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class ModelTrainer(private val context: Context) {

    val serverUrl = "https://roblocks-backend.braveisland-15412894.southeastasia.azurecontainerapps.io/train"  // ganti dengan alamat backend Flask

    suspend fun trainModel(class1: List<Bitmap>, class2: List<Bitmap>): File = withContext(
        Dispatchers.IO) {
        val boundary = "Boundary-${UUID.randomUUID()}"
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        val url = URL(serverUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.useCaches = false
        connection.requestMethod = "POST"
        connection.setRequestProperty("Connection", "Keep-Alive")
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        val outputStream = connection.outputStream

        fun writeBitmapField(fieldName: String, bitmap: Bitmap, index: Int) {
            val byteArray = bitmapToByteArray(bitmap)

            val header = StringBuilder()
            header.append(twoHyphens).append(boundary).append(lineEnd)
            header.append("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"image$index.jpg\"").append(lineEnd)
            header.append("Content-Type: image/jpeg").append(lineEnd)
            header.append(lineEnd)

            outputStream.write(header.toString().toByteArray())
            outputStream.write(byteArray)
            outputStream.write(lineEnd.toByteArray())
        }

        class1.forEachIndexed { index, bitmap ->
            writeBitmapField("class1", bitmap, index)
        }

        class2.forEachIndexed { index, bitmap ->
            writeBitmapField("class2", bitmap, index)
        }

        val finish = twoHyphens + boundary + twoHyphens + lineEnd
        outputStream.write(finish.toByteArray())
        outputStream.flush()
        outputStream.close()

        // Get the response
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Server returned: ${connection.responseCode}")
        }

        val modelFile = File(context.cacheDir, "color_classifier.tflite")
        connection.inputStream.use { input ->
            FileOutputStream(modelFile).use { output ->
                input.copyTo(output)
            }
        }

        return@withContext modelFile
    }


    fun downloadModel(url: String): File {
        try {
            Log.d("ModelTrainer", "Connecting to $url")
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            Log.d("ModelTrainer", "Response code: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Server returned HTTP $responseCode")
            }

            val inputStream = connection.inputStream
            val file = File.createTempFile("color_classifier", ".tflite")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            Log.d("ModelTrainer", "Model downloaded to ${file.absolutePath}")
            return file
        } catch (e: Exception) {
            Log.e("ModelTrainer", "Download failed: ${e.message}", e)
            throw e
        }
    }



    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
