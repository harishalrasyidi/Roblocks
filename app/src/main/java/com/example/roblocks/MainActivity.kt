package com.example.roblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.roblocks.data.AppDatabase
import com.example.roblocks.ui.ArtificialIntelligenceScreen
import com.example.roblocks.ui.MainScreen
import com.example.roblocks.ui.RoboticsScreen
import com.example.roblocks.ui.theme.RoblocksTheme

lateinit var appDatabase: AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
        setContent {
            RoblocksTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main_screen"
                ){
                    composable("main_screen") {
                        MainScreen(navController = navController)
                    }
                    composable("artificial_intelligence_screen"){
                        ArtificialIntelligenceScreen(navController = navController)
                    }
                    composable("robotics_screen") {
                        RoboticsScreen(navController = navController)
                    }
                }
            }
        }
    }
}
