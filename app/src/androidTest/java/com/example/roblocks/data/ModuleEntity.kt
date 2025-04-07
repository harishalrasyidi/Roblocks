package com.example.roblocks.data

import androidx.compose.ui.semantics.Role
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.UUID

@Entity(tableName = "Module_table")
data class ModuleEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val email: String,
    val password: String,
    val role: Role,
    val created_at: Time,
)