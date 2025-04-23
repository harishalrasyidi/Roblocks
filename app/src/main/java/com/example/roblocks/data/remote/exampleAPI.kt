package com.example.roblocks.data.remote

import retrofit2.http.GET

interface exampleAPI {
    @GET("Test")
    suspend fun exampleNetworkCall()
}