package com.example.roblocks.api
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.data.entities.QuestionEntity
import dagger.Module
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/api/modules")
    suspend fun getAllModules(): List<ModuleEntity>

    @GET("/api/modules/{moduleId}")
    suspend fun getModuleById(@Path("moduleId") moduleId: String): ModuleEntity

    @GET("/api/modules/{moduleId}/questions")
    suspend fun getQuestions(@Path("moduleId") moduleId: String): List<QuestionEntity>
}


