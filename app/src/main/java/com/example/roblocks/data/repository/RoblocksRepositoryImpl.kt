package com.example.roblocks.data.repository

import com.example.roblocks.data.remote.exampleAPI
import com.example.roblocks.domain.repository.RoblocksRepository

class RoblocksRepositoryImpl(
    private val api:exampleAPI
): RoblocksRepository {
    override suspend fun exampleNetworkCall() {

    }
}