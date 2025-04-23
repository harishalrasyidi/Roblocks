package com.example.roblocks.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "siswa_table",
    foreignKeys = [ForeignKey(
        entity = LecturerEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_lecturer"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SiswaEntity(
    @PrimaryKey val id: String,
    val id_lecturer: String,
    val name: String,
    val email: String,
    val password_hash: String,
    val created_at: Long,
    val updated_at: Long
)
