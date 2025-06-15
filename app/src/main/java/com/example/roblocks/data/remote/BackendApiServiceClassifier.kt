package com.example.roblocks.data.remote

import com.example.roblocks.ai.TrainingResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface BackendApiServiceClassifier {
    @Multipart
    @POST("train")
    suspend fun uploadImages(
        @Query("session_id") sessionId: String,
        @Part images: List<MultipartBody.Part>,
        @Part classLabelParts: List<MultipartBody.Part>,
        @Query("epochs") epochs: Int,
        @Query("batch_size") batchSize: Int,
        @Query("learning_rate") learningRate: Float
    ): TrainingResponse

    @GET("download_model")
    suspend fun downloadModel(@Query("session_id") sessionId: String): ResponseBody
}