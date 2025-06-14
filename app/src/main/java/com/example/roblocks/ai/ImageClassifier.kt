package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import javax.inject.Inject

class ImageClassifier @Inject constructor(
    private val context: Context
) {

    private var objectDetector: ObjectDetector? = null
    private var classLabels: List<String> = emptyList()

    fun loadModel(file: File) {
        try {
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.3f)
                .build()
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                file.absolutePath,
                options
            )
            Log.d("Classifier", "Model loaded from ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("Classifier", "Failed to load model", e)
            throw IllegalStateException("Failed to load model: ${e.message}")
        }
    }

    fun setClassLabels(labels: List<String>) {
        classLabels = labels
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        if (objectDetector == null) {
            throw IllegalStateException("Model not loaded. Call loadModel first.")
        }

        val tensorImage = TensorImage.fromBitmap(bitmap)
        val results: List<Detection> = objectDetector?.detect(tensorImage)
            ?: return "Unknown" to 0f

        val bestDetection = results.maxByOrNull { detection ->
            detection.categories.firstOrNull()?.score ?: 0f
        }

        return if (bestDetection != null && bestDetection.categories.isNotEmpty()) {
            val category = bestDetection.categories[0]
            val label = if (classLabels.isNotEmpty() && category.label.toIntOrNull() != null) {
                classLabels.getOrNull(category.label.toInt()) ?: category.label
            } else {
                category.label
            }
            Log.d("Classifier", "Detected: $label with confidence ${category.score}")
            label to category.score
        } else {
            Log.d("Classifier", "No objects detected")
            "Unknown" to 0f
        }
    }

    fun close() {
        objectDetector?.close()
        objectDetector = null
        Log.d("Classifier", "ObjectDetector closed")
    }
}