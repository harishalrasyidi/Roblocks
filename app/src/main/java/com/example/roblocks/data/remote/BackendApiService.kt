package com.example.roblocks.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface BackendApiService {
    @Multipart
    @POST("train")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>,
        @Part("class_label") classLabels: List<String>,
        @Query("epochs") epochs: Int,
        @Query("batch_size") batchSize: Int,
        @Query("learning_rate") learningRate: Float
    ): Map<String, Any>

    @GET("download_model")
    suspend fun downloadModel(): ResponseBody
}