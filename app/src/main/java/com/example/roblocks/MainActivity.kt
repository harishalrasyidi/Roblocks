package com.example.roblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.roblocks.blockly.BlocklyEditorScreen
import com.example.roblocks.data.AppDatabase
import com.example.roblocks.ui.ArtificialIntelligenceScreen
import com.example.roblocks.ui.BottomNavBar
import com.example.roblocks.ui.LearnScreen
import com.example.roblocks.ui.MainScreen
import com.example.roblocks.ui.ProfileScreen
import com.example.roblocks.ui.RoboticsScreen
import com.example.roblocks.ui.ai.ImageClassifierApp
import com.example.roblocks.ui.theme.RoblocksTheme

lateinit var appDatabase: AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).fallbackToDestructiveMigration()
        .build()
        
        setContent {
            RoblocksTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main_screen",
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
                    composable("profile_screen"){
                        ProfileScreen(navController = navController)
                    }
                    composable("learn_screen") {
                        LearnScreen(navController = navController)
                    }
                    composable("ml_image"){
                        ImageClassifierApp(navController = navController)
                    }
                    composable("blockly_editor_screen") {
                        BlocklyEditorScreen(navController = navController)
                    }
                    composable(
                        route = "blockly_editor_screen?projectId={projectId}",
                        arguments = listOf(
                            navArgument("projectId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val projectId = backStackEntry.arguments?.getString("projectId")
                        BlocklyEditorScreen(
                            navController = navController,
                            projectId = projectId
                        )
                    }
                }
            }
        }
    }
}
