package com.example.roblocks.ui.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

class ImageClassifier(private val context: Context) {

    private var modelFile: File? = null
    private var class1MeanColor: Int? = null
    private var class2MeanColor: Int? = null

    fun loadModel(file: File) {
        modelFile = file
        // Optional: logika tambahan untuk validasi file bisa dimasukkan di sini
    }

    fun setTrainingData(class1: List<Bitmap>, class2: List<Bitmap>) {
        class1MeanColor = calculateAverageColor(class1)
        class2MeanColor = calculateAverageColor(class2)
    }

    fun classify(bitmap: Bitmap): Pair<String, Float> {
        val imageColor = calculateAverageColor(bitmap)

        val distToClass1 = colorDistance(imageColor, class1MeanColor!!)
        val distToClass2 = colorDistance(imageColor, class2MeanColor!!)

        val confidence: Float
        val prediction: String

        if (distToClass1 < distToClass2) {
            prediction = "Class 1"
            confidence = (1f - (distToClass1 / (distToClass1 + distToClass2)))
        } else {
            prediction = "Class 2"
            confidence = (1f - (distToClass2 / (distToClass1 + distToClass2)))
        }

        return prediction to confidence
    }

    private fun calculateAverageColor(images: List<Bitmap>): Int {
        val rTotal = images.sumOf { bitmap -> bitmap.averageRed() }
        val gTotal = images.sumOf { bitmap -> bitmap.averageGreen() }
        val bTotal = images.sumOf { bitmap -> bitmap.averageBlue() }
        val count = images.size
        return Color.rgb(rTotal / count, gTotal / count, bTotal / count)
    }

    private fun calculateAverageColor(bitmap: Bitmap): Int {
        return Color.rgb(bitmap.averageRed(), bitmap.averageGreen(), bitmap.averageBlue())
    }

    private fun colorDistance(c1: Int, c2: Int): Float {
        val r1 = Color.red(c1)
        val g1 = Color.green(c1)
        val b1 = Color.blue(c1)
        val r2 = Color.red(c2)
        val g2 = Color.green(c2)
        val b2 = Color.blue(c2)

        return sqrt((r1 - r2).toDouble().pow(2.0) +
                (g1 - g2).toDouble().pow(2.0) +
                (b1 - b2).toDouble().pow(2.0)).toFloat()
    }

    private fun Bitmap.averageRed(): Int = calculateChannelAverage { Color.red(it) }
    private fun Bitmap.averageGreen(): Int = calculateChannelAverage { Color.green(it) }
    private fun Bitmap.averageBlue(): Int = calculateChannelAverage { Color.blue(it) }

    private fun Bitmap.calculateChannelAverage(channel: (Int) -> Int): Int {
        var sum = 0L
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        for (pixel in pixels) {
            sum += channel(pixel)
        }
        return (sum / pixels.size).toInt()
    }
}