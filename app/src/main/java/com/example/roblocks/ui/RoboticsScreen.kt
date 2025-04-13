package com.example.roblocks.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.blockly.BlocklyViewModel
import com.example.roblocks.blockly.BlocklyViewModelFactory
import com.example.roblocks.data.ProjectIOTEntity
import com.example.roblocks.data.ProjectIOTRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoboticsScreen(navController: NavController) {

    val selectedIndex = remember { mutableStateOf(2) }
    val context = LocalContext.current
    val repository = remember { ProjectIOTRepository.getInstance(context) }
    
    // Collect projects as State
    val projects = remember { repository.getAllProjects() }.collectAsState(initial = emptyList())

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("blockly_editor_screen") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create New Project"
                )
            }
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

            // Menampilkan daftar proyek jika ada
            if (projects.value.isNotEmpty()) {
                Text(
                    text = "Project Robotics/IOT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyColumn {
                    items(projects.value) { project ->
                        ProjectItem(
                            project = project,
                            onClick = {
                                // Navigate to editor with project ID
                                navController.navigate("blockly_editor_screen?projectId=${project.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                // Jika belum ada proyek, tampilkan ProjectCard biasa
                ProjectCard("Project Robotics/IOT", false)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ArticleCard(
                icon = R.drawable.ic_buku,
                title = "Learn",
                description = "Pelajari Tentang Robotika dan Internet Of Things"
            )
            
            // Info card about BlocklyDuino
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate("blockly_editor_screen") },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "BlocklyDuino Editor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Buat program Arduino dengan mudah menggunakan visual block programming",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectItem(
    project: ProjectIOTEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val updatedDate = dateFormat.format(Date(project.updated_at))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon/Avatar for project
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "Updated: $updatedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (project.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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