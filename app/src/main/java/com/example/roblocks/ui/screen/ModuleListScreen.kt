package com.example.roblocks.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel

@Composable
fun ModuleListScreen(
    navController: NavController,
    viewModel: ModuleQuizViewModel = viewModel(),
    onModuleClick: (ModuleEntity) -> Unit = {
        navController.navigate("module_detail_screen/${it.id}")
    }
) {
    LaunchedEffect(Unit) {
        viewModel.insertSampleData()
    }

    val modules by viewModel.allModules.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(modules) { module ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onModuleClick(module) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = module.title, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
