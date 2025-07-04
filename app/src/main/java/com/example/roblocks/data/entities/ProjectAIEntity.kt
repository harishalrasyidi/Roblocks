package com.example.roblocks.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//@Entity(tableName = "ProyekAI_table",
//    foreignKeys = [ForeignKey(
//        entity = SiswaEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["id_siswa"],
//        onDelete = ForeignKey.CASCADE
//    )]
//)

@Entity(tableName = "ProyekAI_table")
data class ProjectAIEntity(
    @PrimaryKey override val id: String,
    override val name: String,
    override val tipe: String,
    val file_source_proyek_AI: String,
    override val created_at: Long,
    override val updated_at: Long,
    val workspace_xml: String,
    val id_siswa: String
): ProjectEntity
