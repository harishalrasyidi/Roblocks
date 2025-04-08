package com.example.roblocks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_iot")
data class ProjectIOTEntity(
    @PrimaryKey val id: String,
    val id_siswa: String,
    val name: String,
    val description: String,
    val file_block_code: String, // Link .cdk
    val file_source_code: String, // Link .ino
    val created_at: Long,
    val updated_at: Long
) 