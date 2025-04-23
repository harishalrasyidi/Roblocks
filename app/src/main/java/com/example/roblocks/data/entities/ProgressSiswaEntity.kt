package com.example.roblocks.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ProgressSiswa_table",
    foreignKeys = [
        ForeignKey(
            entity = SiswaEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_siswa"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["module_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgressSiswaEntity(
    @PrimaryKey val id: String,
    val id_siswa: String,
    val module_id: String,
    val progress_percentage: Float,
    val is_completed: Boolean,
    val completed_at: Long?,
    val created_at: Long,
    val updated_at: Long,
    val score: Int
)
