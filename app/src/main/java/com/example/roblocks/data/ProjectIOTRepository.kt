package com.example.roblocks.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectIOTRepository(private val projectIOTDao: ProjectIOTDao) {
    companion object {
        private const val TAG = "ProjectIOTRepository"
    }
    
    // Get all projects
    fun getAllProjects(): Flow<List<ProjectIOTEntity>> {
        return projectIOTDao.getAllProjects()
    }
    
    // Get projects by student ID
    fun getProjectsByStudentId(studentId: String): Flow<List<ProjectIOTEntity>> {
        return projectIOTDao.getProjectsByStudentId(studentId)
    }
    
    // Get a project by ID
    suspend fun getProjectById(id: String): ProjectIOTEntity? {
        return projectIOTDao.getProjectById(id)
    }
    
    // Create a new project
    suspend fun createProject(
        studentId: String,
        name: String,
        description: String,
        blockXml: String,
        generatedCode: String
    ): ProjectIOTEntity {
        val id = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        
        // In a real app, these would be actual Google Drive file URLs
        val blockCodeFileName = "PROJECT_${id}.cdk"
        val sourceCodeFileName = "PROJECT_${id}.ino"
        
        val project = ProjectIOTEntity(
            id = id,
            id_siswa = studentId,
            name = name,
            description = description,
            file_block_code = blockCodeFileName,
            file_source_code = sourceCodeFileName,
            created_at = timestamp,
            updated_at = timestamp
        )
        
        Log.d(TAG, "Creating new project: $project")
        projectIOTDao.insertProject(project)
        return project
    }
    
    // Update an existing project
    suspend fun updateProject(
        id: String,
        name: String,
        description: String,
        blockXml: String,
        generatedCode: String
    ): Boolean {
        val existingProject = projectIOTDao.getProjectById(id) ?: return false
        
        val updatedProject = existingProject.copy(
            name = name,
            description = description,
            updated_at = System.currentTimeMillis()
        )
        
        Log.d(TAG, "Updating project: $updatedProject")
        projectIOTDao.updateProject(updatedProject)
        return true
    }
    
    // Delete a project
    suspend fun deleteProject(id: String): Boolean {
        Log.d(TAG, "Deleting project with ID: $id")
        projectIOTDao.deleteProjectById(id)
        return true
    }
} 