package com.example.roblocks.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectIOTDao {
    @Query("SELECT * FROM project_iot ORDER BY updated_at DESC")
    fun getAllProjects(): Flow<List<ProjectIOTEntity>>
    
    @Query("SELECT * FROM project_iot WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectIOTEntity?
    
    @Query("SELECT * FROM project_iot WHERE id_siswa = :studentId ORDER BY updated_at DESC")
    fun getProjectsByStudentId(studentId: String): Flow<List<ProjectIOTEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectIOTEntity)
    
    @Update
    suspend fun updateProject(project: ProjectIOTEntity)
    
    @Delete
    suspend fun deleteProject(project: ProjectIOTEntity)
    
    @Query("DELETE FROM project_iot WHERE id = :id")
    suspend fun deleteProjectById(id: String)
} 