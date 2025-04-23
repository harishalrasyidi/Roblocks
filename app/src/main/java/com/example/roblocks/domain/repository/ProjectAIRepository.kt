package com.example.roblocks.domain.repository

import com.example.roblocks.data.entities.ProjectAIEntity
import kotlinx.coroutines.flow.Flow

interface ProjectAIRepository {
    fun getAllProjects(): Flow<List<ProjectAIEntity>>
    suspend fun getProjectById(id: String): ProjectAIEntity?
    suspend fun saveProject(
        name: String,
        description: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectAIEntity
    suspend fun updateProject(project: ProjectAIEntity)
    suspend fun deleteProject(project: ProjectAIEntity)
}
