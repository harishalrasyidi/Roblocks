package com.example.roblocks.ui.screen

import android.os.Process
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.domain.viewModel.AuthViewModel
import com.example.roblocks.domain.viewModel.RoblocksViewModel
import com.example.roblocks.ui.BottomNavBar
import com.example.roblocks.ui.component.FeatureCardWithRobot
import com.example.roblocks.ui.component.TrackProgressCard

@Composable
fun MainScreen(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = hiltViewModel()
    val roblocksViewModel: RoblocksViewModel = hiltViewModel()
    val user = authViewModel.getCurrentUser()

    Scaffold(
        bottomBar = {
            BottomNavBar(selectedIndex = selectedIndex.value) {
                selectedIndex.value = it
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Hello,", color = Color.Gray)
                        Text(
                            text = user?.displayName ?: user?.providerData?.firstOrNull { it.displayName != null }?.displayName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    IconButton(
                        onClick = { showExitDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Red.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Exit app",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

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
                            onClick = { navController.navigate("profile_screen") }
                        )
                    }
                }
            }

            // Exit confirmation dialog
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = {
                        Text(text = "Keluar Aplikasi", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text("Apakah Anda yakin ingin keluar dari aplikasi?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                authViewModel.signout()
                                showExitDialog = false
                                // Wait for signout to complete before navigating
                                if (authViewModel.authState.value is AuthViewModel.AuthState.Unauthenticated) {
                                    navController.navigate("login_screen") {
                                        popUpTo("main_screen") { inclusive = true }
                                    }
                                }
                            }
                        ) {
                            Text("Ya", color = Color(0xFFC8412A))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showExitDialog = false }
                        ) {
                            Text("Tidak")
                        }
                    }
                )
            }

            // Add an observer for auth state changes
            LaunchedEffect(authViewModel.authState) {
                when (authViewModel.authState.value) {
                    is AuthViewModel.AuthState.Unauthenticated -> {
                        navController.navigate("login_screen") {
                            popUpTo("main_screen") { inclusive = true }
                        }
                    }
                    is AuthViewModel.AuthState.Error -> {
                        // Handle error if needed
                    }
                    else -> {}
                }
            }
        }
    }
}
