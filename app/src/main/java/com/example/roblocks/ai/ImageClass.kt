package com.example.roblocks.ai

import android.graphics.Bitmap

data class ImageClass(
    val name: String,
    val imageCount: Int = 0,
    val images: List<Bitmap> = emptyList()
)