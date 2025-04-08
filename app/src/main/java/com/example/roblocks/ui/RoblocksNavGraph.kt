package com.example.roblocks.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roblocks.ui.screens.BlockEditorScreen
import com.example.roblocks.viewmodel.BlockEditorViewModel

// Define the routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BlockEditor : Screen("block_editor")
}

@Composable
fun RoblocksNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            MainScreen(
                onIotClick = {
                    navController.navigate(Screen.BlockEditor.route)
                }
            )
        }
        
        composable(Screen.BlockEditor.route) {
            val viewModel: BlockEditorViewModel = viewModel()
            BlockEditorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}