package com.example.roblocks.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectAIDao {
    @Query("SELECT * FROM ProyekAI_table ORDER BY updated_at DESC")
    fun getAllProjectsNewest(): Flow<List<ProjectAIEntity>>

    @Query("SELECT * FROM ProyekAI_table  ORDER BY updated_at DESC LIMIT 1")
    fun getProjectNewest(): ProjectAIEntity

    @Query("SELECT * FROM ProyekAI_table WHERE id = :id")
    fun getProjectById(id: String): ProjectAIEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectAIEntity)
    
    @Update
    suspend fun updateProject(project: ProjectAIEntity)
    
    @Delete
    suspend fun deleteProject(project: ProjectAIEntity)
} 