package com.example.roblocks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ProyekRobot_table")
data class ProjectIOTEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val file_block_code: String, // Link ke file XML blockly
    val file_source_code: String, // Link ke file Arduino .ino
    val workspace_xml: String,    // Langsung simpan XML workspace dalam database
    val created_at: Long,
    val updated_at: Long
)
