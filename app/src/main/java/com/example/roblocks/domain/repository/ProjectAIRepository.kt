package com.example.roblocks.domain.repository

import android.content.Context
import com.example.roblocks.data.entities.ProjectAIEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipFile

interface ProjectAIRepository {
    fun getProjectNewest(): ProjectAIEntity
    fun getAllProjects(): Flow<List<ProjectAIEntity>>
    suspend fun getProjectById(id: String): ProjectAIEntity?
    suspend fun saveProject(
        name: String,
        tipe: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectAIEntity
    suspend fun insertProject(project: ProjectAIEntity)
    suspend fun updateProject(project: ProjectAIEntity)
    suspend fun deleteProject(project: ProjectAIEntity)
    suspend fun exportProjectAsZip(
        context: Context,
        project: ProjectAIEntity,
        modelFile: File,
        datasetDir: File
    ): File
    suspend fun deleteProjectByID(id: String)
}
