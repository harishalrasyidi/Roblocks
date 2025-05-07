package com.example.roblocks.domain.repository

import com.example.roblocks.data.entities.ProjectIOTEntity
import kotlinx.coroutines.flow.Flow

interface ProjectIOTRepository {
    fun getAllProjects(): Flow<List<ProjectIOTEntity>>
    suspend fun getProjectById(id: String): ProjectIOTEntity?
    suspend fun saveProject(
        name: String,
        tipe: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectIOTEntity
    suspend fun insertProject(project: ProjectIOTEntity)
    suspend fun updateProject(project: ProjectIOTEntity)
    suspend fun deleteProject(project: ProjectIOTEntity)
    suspend fun deleteProjectByID(id: String)

}