package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.domain.viewModel.ProjectIOTViewModel
import com.example.roblocks.ui.component.ArticleCard
import com.example.roblocks.ui.BottomNavBar
import com.example.roblocks.ui.component.ProjectCard
import com.example.roblocks.ui.component.CardJenisProyek
import androidx.compose.runtime.collectAsState
import com.example.roblocks.domain.viewModel.ProjectAIViewModel


@Composable
fun ArtificialIntelligenceScreen(navController: NavController) {

    val projectAIViewModel: ProjectAIViewModel = hiltViewModel()

    val projectList by projectAIViewModel.getAllProject()
        .collectAsState(initial = emptyList())

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
            ProjectCard("Project AI", false, "AI", navController = navController, projectList )

            Spacer(modifier = Modifier.height(24.dp))

            ArticleCard(
                icon = R.drawable.ic_buku,
                title = "Learn",
                description = "Apasih Machine Learning/Artificial Intelligence Itu?"
            )
        }
    }
}

@Composable
fun CreateProjectDialogAI(onDismiss: () -> Unit, navController: NavController) {
    val namaProyek = remember { mutableStateOf("") }

    AlertDialog(
        containerColor = Color(0xFF4A65FE),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Proyek AI Baru",
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

                        CardJenisProyek("Klasifikasi Gambar", R.drawable.ic_ai, "Buatlah suatu model yang bisa tahu maksud dari  gambar yang kamu kirim!", "ml_image", navController = navController, "AI")
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