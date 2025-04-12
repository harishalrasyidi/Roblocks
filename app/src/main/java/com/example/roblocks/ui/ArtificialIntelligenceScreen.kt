package com.example.roblocks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.roblocks.R

@Composable
fun ArtificialIntelligenceScreen(navController: NavController) {

    val selectedIndex = remember { mutableStateOf(1) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = {
                        index ->
                    when(index){
                        0 -> navController.navigate("main_screen")
                        1 -> {}
                        2 -> navController.navigate("robotics_screen")
                        3 -> navController.navigate("learn_screen")
                        4 -> navController.navigate("profile_screen")
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
            Text("Artificial Intelligence", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            //tambah kondisi jadi ada list project waktu DAO udah beres
            ProjectCard("Project AI", false)

            Spacer(modifier = Modifier.height(24.dp))

            ArticleCard(
                icon = R.drawable.ic_buku,
                title = "Learn",
                description = "Apasih Machine Learning/Artificial Intelligence Itu?"
            )
        }
    }
}