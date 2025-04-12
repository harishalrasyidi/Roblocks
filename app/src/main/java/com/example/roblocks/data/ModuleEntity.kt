package com.example.roblocks.data

import androidx.compose.ui.semantics.Role
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.UUID

@Entity(tableName = "Module_table")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val created_at: Long,
    val updated_at: Long,
    val link_video: String
)
