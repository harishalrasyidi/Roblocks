package com.example.roblocks.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//@Entity(tableName = "ProyekRobot_table",
//    foreignKeys = [ForeignKey(
//        entity = SiswaEntity::class,
//        parentColumns = ["id"],
//        childColumns = ["id_siswa"],
//        onDelete = ForeignKey.CASCADE
//    )]
//)

@Entity(tableName = "ProyekRobot_table")
data class ProjectIOTEntity(
    @PrimaryKey val id: String,
    override val name: String,
    val description: String,
    val file_block_code: String, // Link ke file XML blockly
    val file_source_code: String, // Link ke file Arduino .ino
    val workspace_xml: String,    // Langsung simpan XML workspace dalam database
    val created_at: Long,
    val updated_at: Long,
): ProjectEntity
