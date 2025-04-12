package com.example.roblocks.data

import androidx.compose.ui.semantics.Role
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.UUID

@Entity(tableName = "Question_table",
    foreignKeys = [ForeignKey(
        entity = ModuleEntity::class,
        parentColumns = ["id"],
        childColumns = ["module_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val module_id: String,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_answer: String,
    val created_at: Long,
    val updated_at: Long
)
