package com.example.roblocks.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel
import com.example.roblocks.ui.BottomNavBar

@Composable
fun ModuleListScreen(
    navController: NavController,
    viewModel: ModuleQuizViewModel = hiltViewModel(),
    onModuleClick: (ModuleEntity) -> Unit = {
        navController.navigate("module_detail_screen/${it.id}")
    }
) {
    val selectedIndex = remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        viewModel.insertSampleData()
    }

    val modules by viewModel.allModules.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = { index ->
                    selectedIndex.value = index
                    when (index) {
                        0 -> navController.navigate("main_screen")
                        1 -> navController.navigate("artificial_intelligence_screen")
                        2 -> navController.navigate("robotics_screen")
                        3 -> {}
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
                .padding(16.dp)
        ) {
            Text(
                text = "Pilih Sub Tema",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .background(
                        color = Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(modules) { module ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color(0xFF5392D7), shape = MaterialTheme.shapes.medium)
                            .border(2.dp, Color.White, shape = MaterialTheme.shapes.medium)
                            .clickable { onModuleClick(module) }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White, shape = MaterialTheme.shapes.small)
                                .border(1.dp, Color.White, shape = MaterialTheme.shapes.small)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = module.title,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
