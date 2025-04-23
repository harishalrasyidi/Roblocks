package com.example.roblocks.data.repository

import com.example.roblocks.data.DAO.ProjectAIDao
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectAIRepositoryImpl(private val projectAIDao: ProjectAIDao) : ProjectAIRepository {

    fun getAllProjects(): Flow<List<ProjectAIEntity>> {
        return projectAIDao.getAllProjectsNewest()
    }

    suspend fun getProjectById(id: String): ProjectAIEntity? {
        return projectAIDao.getProjectById(id)
    }

    suspend fun saveProject(
        name: String,
        description: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectAIEntity {
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val aiProjectFileName = "PROJECT_${uuid}.ai"
        val project = ProjectAIEntity(
            id = uuid,
            name = name,
            description = description,
            workspace_xml = blocklyXml,
            created_at = timestamp,
            updated_at = timestamp,
            file_source_proyek_AI = aiProjectFileName,
            id_siswa = "testSaja"
        )

        projectAIDao.insertProject(project)
        return project
    }

    suspend fun updateProject(project: ProjectAIEntity) {
        val updatedProject = project.copy(
            updated_at = System.currentTimeMillis()
        )
        projectAIDao.updateProject(updatedProject)
    }

    suspend fun deleteProject(project: ProjectAIEntity) {
        projectAIDao.deleteProject(project)
    }
}
