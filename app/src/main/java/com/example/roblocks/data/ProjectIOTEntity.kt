package com.example.roblocks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ProyekRobot_table",
        foreignKeys = [ForeignKey(
        entity = SiswaEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_siswa"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProjectIOTEntity(
    @PrimaryKey val id: String,
    val id_siswa: String,
    val name: String,
    val description: String,
    val file_block_code: String,
    val file_source_code: String,
    val created_at: Long,
    val updated_at: Long
)
