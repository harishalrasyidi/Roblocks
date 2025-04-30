package com.example.roblocks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roblocks.data.DAO.ProjectAIDao
import com.example.roblocks.data.DAO.ProjectIOTDao
import com.example.roblocks.data.entities.AdminEntity
import com.example.roblocks.data.entities.LecturerEntity
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.data.entities.ProgressSiswaEntity
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.data.entities.QuestionEntity
import com.example.roblocks.data.entities.SiswaEntity
import com.example.roblocks.data.repository.ProjectIOTRepositoryImpl

@Database(
    entities = [
        AdminEntity::class,
        LecturerEntity::class,
        SiswaEntity::class,
        ModuleEntity::class,
        QuestionEntity::class,
        ProgressSiswaEntity::class,
        ProjectAIEntity::class,
        ProjectIOTEntity::class
    ],
    version = 4
)
//belum ada DAO

abstract class AppDatabase : RoomDatabase() {
//    abstract fun adminDao(): AdminDao
//    abstract fun lecturerDao(): LecturerDao
//    abstract fun siswaDao(): SiswaDao
//    abstract fun moduleDao(): ModuleDao
//    abstract fun questionDao(): QuestionDao
//    abstract fun userProgressDao(): UserProgressDao
    abstract fun projectAIDao(): ProjectAIDao
    abstract fun projectIOTDao(): ProjectIOTDao
    companion object {
        @Volatile
        private var INSTANCE: ProjectIOTRepositoryImpl? = null

        fun getInstance(context: Context): ProjectIOTRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                val instance = ProjectIOTRepositoryImpl(database.projectIOTDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
