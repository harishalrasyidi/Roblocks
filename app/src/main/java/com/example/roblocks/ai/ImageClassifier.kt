package com.example.roblocks.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class ImageClassifier @Inject constructor(
    private val context: Context
) {
    private var interpreter: Interpreter? = null
    private var classLabels: List<String> = emptyList()

    private var inputWidth: Int = 0
    private var inputHeight: Int = 0

    fun loadModel(file: File) {
        if (!file.exists() || file.length() == 0L) {
            val errorMsg = "Model file does not exist or is empty: ${file.absolutePath}"
            Log.e("Classifier", errorMsg)
            throw IllegalStateException(errorMsg)
        }

        try {
            val options = Interpreter.Options()
            // It's good practice to add a delegate for performance, e.g., NNAPI
            // options.addDelegate(NnApiDelegate())
            interpreter = Interpreter(file, options)

            // Get model input shape
            val inputTensor = interpreter!!.getInputTensor(0)
            inputWidth = inputTensor.shape()[1]
            inputHeight = inputTensor.shape()[2]

            Log.d("Classifier", "Model loaded from ${file.absolutePath}. Input shape: [${inputTensor.shape().joinToString()}]")
        } catch (e: Exception) {
            Log.e("Classifier", "Failed to load model", e)
            throw IllegalStateException("Failed to initialize TFLite Interpreter: ${e.message}", e)
        }
    }

    // This function is now critically important
    fun setClassLabels(labels: List<String>) {
        this.classLabels = labels
        Log.d("Classifier", "Class labels set: $labels")
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        if (interpreter == null) {
            throw IllegalStateException("Model not loaded. Call loadModel first.")
        }
        if (classLabels.isEmpty()) {
            throw IllegalStateException("Class labels not set. Call setClassLabels first.")
        }

        // 1. PRE-PROCESSING: Resize and Normalize the image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputHeight, inputWidth, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            // Your model expects Float32 values in [0,1]
            // NormalizationOp(mean, std) = (pixel - mean) / std
            .add(NormalizeOp(0.0f, 255.0f))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val inputBuffer: ByteBuffer = tensorImage.buffer

        // 2. RUN INFERENCE: Get the output
        val outputTensor = interpreter!!.getOutputTensor(0)
        val outputShape = outputTensor.shape()
        val outputDataType = outputTensor.dataType()

        // Create a buffer for the output
        val outputBuffer = ByteBuffer.allocateDirect(outputTensor.numBytes())
        outputBuffer.order(java.nio.ByteOrder.nativeOrder())

        interpreter!!.run(inputBuffer, outputBuffer)

        // 3. POST-PROCESSING: Find the best result
        outputBuffer.rewind()
        val probabilities = FloatArray(outputShape[1])
        outputBuffer.asFloatBuffer().get(probabilities)

        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1

        return if (maxIndex != -1) {
            val label = classLabels[maxIndex]
            val confidence = probabilities[maxIndex]
            Log.d("Classifier", "Classified: $label with confidence $confidence")
            label to confidence
        } else {
            Log.d("Classifier", "No classifications found")
            "Unknown" to 0f
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        Log.d("Classifier", "Interpreter closed")
    }
}