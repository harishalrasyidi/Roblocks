package com.example.roblocks.data

import androidx.room.Database
import androidx.room.RoomDatabase

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
    version = 1
)
//belum ada DAO

abstract class AppDatabase : RoomDatabase() {
//    abstract fun adminDao(): AdminDao
//    abstract fun lecturerDao(): LecturerDao
//    abstract fun siswaDao(): SiswaDao
//    abstract fun moduleDao(): ModuleDao
//    abstract fun questionDao(): QuestionDao
//    abstract fun userProgressDao(): UserProgressDao
//    abstract fun projectAIDao(): ProjectAIDao
    abstract fun projectIOTDao(): ProjectIOTDao
}
