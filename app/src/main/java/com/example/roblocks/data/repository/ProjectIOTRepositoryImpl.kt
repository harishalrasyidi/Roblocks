package com.example.roblocks.data.repository

import com.example.roblocks.data.DAO.ProjectIOTDao
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.repository.ProjectIOTRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class ProjectIOTRepositoryImpl @Inject constructor(
    @ApplicationContext private val projectIOTDao: ProjectIOTDao
) : ProjectIOTRepository {

    override fun getAllProjects(): Flow<List<ProjectIOTEntity>> {
        return projectIOTDao.getAllProjectsNewest()
    }

    override suspend fun getProjectById(id: String): ProjectIOTEntity? {
        return projectIOTDao.getProjectById(id)
    }

    override suspend fun saveProject(
        name: String,
        tipe: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectIOTEntity {
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val blockCodeFileName = "PROJECT_${uuid}.cdk"
        val sourceCodeFileName = "PROJECT_${uuid}.ino"

        val project = ProjectIOTEntity(
            id = uuid,
            name = name,
            tipe = tipe,
            file_block_code = blockCodeFileName,
            file_source_code = sourceCodeFileName,
            workspace_xml = blocklyXml,
            created_at = timestamp,
            updated_at = timestamp,
        )

        projectIOTDao.insertProject(project)
        return project
    }

    override suspend fun insertProject(project: ProjectIOTEntity){
        projectIOTDao.insertProject(project)
    }

    override suspend fun updateProject(project: ProjectIOTEntity) {
        val updatedProject = project.copy(updated_at = System.currentTimeMillis())
        projectIOTDao.updateProject(updatedProject)
    }

    override suspend fun deleteProject(project: ProjectIOTEntity) {
        projectIOTDao.deleteProject(project)
    }

    override suspend fun deleteProjectByID(id: String) {
        try {
            val rows = projectIOTDao.deleteProjectById(id)
            Log.d("ProjectIOTRepository", "Rows deleted: $rows for id: $id")
        } catch (e: Exception) {
            Log.e("ProjectIOTRepository", "Delete failed", e)
        }
    }
}
