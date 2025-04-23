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
        class1Images: List<Bitmap>,
        class2Images: List<Bitmap>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://192.168.1.11:5000/train") // Ganti IP sesuai backend kamu
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

            // Upload Class 1 Images
            class1Images.forEachIndexed { index, bitmap ->
                val file = createTempFile(context, bitmap, "class1_img_$index.jpg")
                writeFilePart(outputStream, file, "class1", boundary)
            }

            // Upload Class 2 Images
            class2Images.forEachIndexed { index, bitmap ->
                val file = createTempFile(context, bitmap, "class2_img_$index.jpg")
                writeFilePart(outputStream, file, "class2", boundary)
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
        boundary: String
    ) {
        val twoHyphens = "--"
        val lineEnd = "\r\n"
        val mimeType = "image/jpeg"

        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"${URLEncoder.encode(file.name, "UTF-8")}\"$lineEnd")
        outputStream.writeBytes("Content-Type: $mimeType$lineEnd")
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