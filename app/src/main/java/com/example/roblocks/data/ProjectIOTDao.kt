package com.example.roblocks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectIOTDao {
    @Query("SELECT * FROM ProyekRobot_table ORDER BY updated_at DESC")
    fun getAllProjects(): Flow<List<ProjectIOTEntity>>
    
    @Query("SELECT * FROM ProyekRobot_table WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectIOTEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectIOTEntity)
    
    @Update
    suspend fun updateProject(project: ProjectIOTEntity)
    
    @Delete
    suspend fun deleteProject(project: ProjectIOTEntity)
} 