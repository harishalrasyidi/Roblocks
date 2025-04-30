package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.ui.BottomNavBar
import com.example.roblocks.ui.component.FeatureCardWithRobot
import com.example.roblocks.ui.component.TrackProgressCard

@Composable
fun MainScreen(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }

    // Only one Scaffold should be used here
    Scaffold(
        bottomBar = {
            BottomNavBar(selectedIndex = selectedIndex.value) {
                selectedIndex.value = it
                // Handle navigation based on the selected index
                when (it) {
                    0 -> navController.navigate("main_screen")
                    1 -> navController.navigate("artificial_intelligence_screen")
                    2 -> navController.navigate("robotics_screen")
                    3 -> navController.navigate("learn_screen")
                    4 -> navController.navigate("profile_screen")
                }
            }
        },
        containerColor = Color(0xFFF9F9FF)
    ) { innerPadding ->
        // Ensure this layout is inside the single Scaffold and padding is applied correctly
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)  // Padding from Scaffold
                .padding(20.dp)
        ) {
            Text("Hello,", color = Color.Gray)
            Text("Simaju", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            TrackProgressCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Ready to invent?",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9370DB),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    FeatureCardWithRobot(
                        robotImage = R.drawable.robo1,
                        icon = R.drawable.ic_ai,
                        title = "Artificial Intelligence",
                        backgroundColor = Color(0xFF3366FF),
                        onClick = { navController.navigate("artificial_intelligence_screen") }
                    )
                }
                item {
                    FeatureCardWithRobot(
                        robotImage = R.drawable.robo2,
                        icon = R.drawable.ic_robot,
                        title = "Robotic /IoT",
                        backgroundColor = Color(0xFFFFC107),
                        onClick = { navController.navigate("robotics_screen") }
                    )
                }
                item {
                    FeatureCardWithRobot(
                        robotImage = R.drawable.robo3,
                        icon = R.drawable.ic_buku,
                        title = "Learn",
                        backgroundColor = Color(0xFFFF4D4D),
                        onClick = { navController.navigate("learn_screen") }
                    )
                }
                item {
                    FeatureCardWithRobot(
                        robotImage = R.drawable.robo4,
                        icon = R.drawable.ic_profile,
                        title = "Profile",
                        backgroundColor = Color(0xFF00CFFF),
                        onClick = { /* Handle Profile click */ }
                    )
                }
            }
        }
    }
}

