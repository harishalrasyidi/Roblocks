package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.roblocks.ui.BottomNavBar


@Composable
fun ProfileScreen(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(4) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("main_screen")
                        1 -> navController.navigate("artificial_intelligence_screen")
                        2 -> navController.navigate("robotics_screen")
                        3 -> navController.navigate("learn_screen")
                        4 -> {}
                    }
                }
            )
        },
        containerColor = Color(0xFFF9F9FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text("Halaman,", color = Color.Gray)
            Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
