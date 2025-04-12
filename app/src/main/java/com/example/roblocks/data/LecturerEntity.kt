package com.example.roblocks.data

import androidx.compose.ui.semantics.Role
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.UUID

@Entity(tableName = "Lecturer_table")
data class LecturerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password_hash: String,
    val created_at: Long,
    val updated_at: Long
)
