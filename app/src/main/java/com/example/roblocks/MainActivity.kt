package com.example.roblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.roblocks.blockly.BlocklyEditorScreen
import com.example.roblocks.data.AppDatabase
import com.example.roblocks.ui.screen.ArtificialIntelligenceScreen
import com.example.roblocks.ui.screen.LearnScreen
import com.example.roblocks.ui.screen.MainScreen
import com.example.roblocks.ui.screen.ProfileScreen
import com.example.roblocks.ui.screen.RoboticsScreen
import com.example.roblocks.ai.ImageClassifierApp
import com.example.roblocks.ui.screen.ModuleDetailScreen
import com.example.roblocks.ui.screen.QuizScreen
import com.example.roblocks.ui.screens.ModuleListScreen
import com.example.roblocks.ui.theme.RoblocksTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_android_components_ViewModelComponent

lateinit var appDatabase: AppDatabase

@AndroidEntryPoint
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
                    composable(
                        route = "ml_image?projectID={projectID}",
                        arguments = listOf(
                            navArgument("projectID") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ){
                        backStackEntry ->
                        val projectID = backStackEntry.arguments?.getString("projectID")
                        ImageClassifierApp(
                            navController = navController,
                            projectID = projectID
                        )
                    }
                    composable("ml_image"){
                        ImageClassifierApp(navController = navController)
                    }
                    composable("blockly_editor_screen") {
                        BlocklyEditorScreen(navController = navController)
                    }
                    composable("quiz_screen/{moduleId}") { backStackEntry ->
                        val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                        QuizScreen(navController = navController, moduleId = moduleId)
                    }

                    composable("learn_screen") {
                        ModuleListScreen(
                            navController = navController,
                            onModuleClick = { module ->
                                navController.navigate("module_detail_screen/${module.id}")
                            }
                        )
                    }

                    composable("module_detail_screen/{moduleId}") { backStackEntry ->
                        val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                        ModuleDetailScreen(navController = navController, moduleId = moduleId)
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
