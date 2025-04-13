package com.example.roblocks.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectIOTRepository(private val projectIOTDao: ProjectIOTDao) {

    fun getAllProjects(): Flow<List<ProjectIOTEntity>> {
        return projectIOTDao.getAllProjects()
    }
    
    suspend fun getProjectById(id: String): ProjectIOTEntity? {
        return projectIOTDao.getProjectById(id)
    }
    
    suspend fun saveProject(
        name: String,
        description: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectIOTEntity {
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        
        // Generate file names
        val blockCodeFileName = "PROJECT_${uuid}.cdk"
        val sourceCodeFileName = "PROJECT_${uuid}.ino"
        
        val project = ProjectIOTEntity(
            id = uuid,
            name = name,
            description = description,
            file_block_code = blockCodeFileName,
            file_source_code = sourceCodeFileName,
            workspace_xml = blocklyXml,
            created_at = timestamp,
            updated_at = timestamp
        )
        
        projectIOTDao.insertProject(project)
        return project
    }
    
    suspend fun updateProject(project: ProjectIOTEntity) {
        val updatedProject = project.copy(
            updated_at = System.currentTimeMillis()
        )
        projectIOTDao.updateProject(updatedProject)
    }
    
    suspend fun deleteProject(project: ProjectIOTEntity) {
        projectIOTDao.deleteProject(project)
    }
    
    companion object {
        @Volatile
        private var INSTANCE: ProjectIOTRepository? = null
        
        fun getInstance(context: Context): ProjectIOTRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                
                val instance = ProjectIOTRepository(database.projectIOTDao())
                INSTANCE = instance
                instance
            }
        }
    }
} 