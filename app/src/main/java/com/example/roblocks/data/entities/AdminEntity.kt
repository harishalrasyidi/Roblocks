package com.example.roblocks.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AdminEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password_hash: String,
    val created_at: Long,
    val updated_at: Long
)
