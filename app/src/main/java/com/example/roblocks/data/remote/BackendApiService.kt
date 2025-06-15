package com.example.roblocks.data.remote

import com.example.roblocks.ai.TrainingResponse
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.data.entities.QuestionEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface BackendApiService {
    @GET("/api/modules")
    suspend fun getAllModules(): List<ModuleEntity>

    @GET("/api/modules/{moduleId}")
    suspend fun getModuleById(@Path("moduleId") moduleId: String): ModuleEntity

    @GET("/api/modules/{moduleId}/questions")
    suspend fun getQuestions(@Path("moduleId") moduleId: String): List<QuestionEntity>
}