package com.example.roblocks.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ModuleEntity::class, ProyekAIEntity::class, ProyekRobotEntity::class, ModuleEntity::class, QuestionEntity::class, ProgressSiswaEntity::class, SiswaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
}