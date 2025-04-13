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
fun RoboticsScreen(navController: NavController) {

    val selectedIndex = remember { mutableStateOf(2) }

    Scaffold(
        bottomBar ={
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = {
                        index ->
                    when(index){
                        0 -> navController.navigate("main_screen")
                        1 -> navController.navigate("artificial_intelligence_screen")
                        2 -> {}
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
            Text("Robotics/IOT", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            //tambah kondisi jadi ada list project waktu DAO udah beres
            ProjectCard("Project Robotics/IOT", false, "Robotics", navController)

            Spacer(modifier = Modifier.height(24.dp))

            ArticleCard(
                icon = R.drawable.ic_buku,
                title = "Learn",
                description = "Pelajari Tentang Robotika dan Internet Of Things"
            )
        }
    }
}

@Composable
fun CreateProjectDialogRobotics(onDismiss: () -> Unit, navController: NavController) {
    val namaProyek = remember { mutableStateOf("") }

    AlertDialog(
        containerColor = Color(0xFF4A65FE),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Proyek Robotik/IOT Baru",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontSize = 18.sp,
                ),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFECD46)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pilih Jenis Proyek",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        cardJenisProyek("Arduino Uno", R.drawable.ic_robot, "Buat Projek Untuk Arduino Uno", "robotik_arduino", navController)
                        Spacer(modifier = Modifier.height(6.dp))
                        cardJenisProyek("Arduino Uno", R.drawable.ic_robot, "Buat Projek Untuk Arduino Uno", "robotik_arduino", navController)
                        Spacer(modifier = Modifier.height(6.dp))
                        cardJenisProyek("Arduino Uno", R.drawable.ic_robot, "Buat Projek Untuk Arduino Uno", "robotik_arduino", navController)
                    }
                }
            }
        },
        confirmButton = {
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                )
            }
        }
    )
}