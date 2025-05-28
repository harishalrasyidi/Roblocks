package com.example.roblocks.api

import com.example.roblocks.data.DAO.ModuleDao
import com.example.roblocks.data.entities.ModuleEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface ModuleApi {
    @GET("modules")
    suspend fun getModules(): List<ModuleEntity>

    @GET("modules/{id}")
    suspend fun getModule(@Path("id") id: String): ModuleEntity
}
