package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class ImageUploader {

    suspend fun uploadImages(
        context: Context,
        images: List<Bitmap>,
        classLabels: List<String>,
        imagesPerClass: List<Int>
    ): Boolean = withContext(Dispatchers.IO) {
        if (images.isEmpty() || classLabels.isEmpty() || imagesPerClass.isEmpty()) {
            Log.e("Uploader", "Empty input: images=${images.size}, labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
            return@withContext false
        }
        if (classLabels.size != imagesPerClass.size) {
            Log.e("Uploader", "Mismatch: labels=${classLabels.size}, imagesPerClass=${imagesPerClass.size}")
            return@withContext false
        }
        if (imagesPerClass.sum() != images.size) {
            Log.e("Uploader", "Total images (${images.size}) does not match sum of imagesPerClass (${imagesPerClass.sum()})")
            return@withContext false
        }

        try {
            val url = URL("https://roblocks-backend.braveisland-15412894.southeastasia.azurecontainerapps.io/train") // Ganti IP sesuai backend
            val boundary = UUID.randomUUID().toString()
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.doOutput = true
            connection.useCaches = false
            connection.requestMethod = "POST"
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

            val outputStream = DataOutputStream(connection.outputStream)

            // Upload images with their corresponding class labels
            var imageIndex = 0
            classLabels.forEachIndexed { classIndex, label ->
                val numImages = imagesPerClass[classIndex]
                for (i in 0 until numImages) {
                    val bitmap = images[imageIndex]
                    val file = createTempFile(context, bitmap, "${label}_img_$i.jpg")
                    writeFilePart(outputStream, file, "images", label, boundary)
                    imageIndex++
                }
            }

            outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            val result = connection.inputStream.bufferedReader().use { it.readText() }

            Log.d("Uploader", "Response code: $responseCode, result: $result")
            responseCode == HttpURLConnection.HTTP_OK

        } catch (e: Exception) {
            Log.e("Uploader", "Upload failed", e)
            false
        }
    }

    private fun createTempFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val file = File(context.cacheDir, fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return file
    }

    private fun writeFilePart(
        outputStream: DataOutputStream,
        file: File,
        fieldName: String,
        classLabel: String,
        boundary: String
    ) {
        val twoHyphens = "--"
        val lineEnd = "\r\n"
        val mimeType = "image/jpeg"

        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"${URLEncoder.encode(file.name, "UTF-8")}\"$lineEnd")
        outputStream.writeBytes("Content-Type: $mimeType$lineEnd")
        outputStream.writeBytes("Content-Class-Label: $classLabel$lineEnd")
        outputStream.writeBytes(lineEnd)

        val fileInputStream = FileInputStream(file)
        val buffer = ByteArray(1024)
        var bytesRead = fileInputStream.read(buffer)

        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bytesRead)
            bytesRead = fileInputStream.read(buffer)
        }

        outputStream.writeBytes(lineEnd)
        fileInputStream.close()
    }
}