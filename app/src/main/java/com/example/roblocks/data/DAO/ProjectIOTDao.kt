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
interface ProjectIOTDao {
    @Query("SELECT * FROM ProyekRobot_table ORDER BY updated_at DESC")
    fun getAllProjectsNewest(): Flow<List<ProjectIOTEntity>>
    
    @Query("SELECT * FROM ProyekRobot_table WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectIOTEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectIOTEntity)

    @Query("DELETE FROM ProyekRobot_table WHERE id = :id")
    fun deleteProjectById(id: String): Int
    
    @Update
    suspend fun updateProject(project: ProjectIOTEntity)
    
    @Delete
    suspend fun deleteProject(project: ProjectIOTEntity)
} 