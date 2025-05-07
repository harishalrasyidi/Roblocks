package com.example.roblocks.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roblocks.data.entities.ModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {

    @Query("SELECT * FROM Module_table")
    suspend fun getAllModules(): List<ModuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    @Query("DELETE FROM Module_table")
    suspend fun clearAllModules()

    @Query("SELECT * FROM Module_table WHERE id = :id")
    fun getModuleById(id: String): Flow<ModuleEntity?>

}