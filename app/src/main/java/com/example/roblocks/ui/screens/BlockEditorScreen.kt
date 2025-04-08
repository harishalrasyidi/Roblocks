package com.example.roblocks.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roblocks.blockly.BlocklyActivity
import com.example.roblocks.viewmodel.BlockEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: BlockEditorViewModel = viewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(key1 = true) {
        // Langsung luncurkan BlocklyActivity
        val intent = Intent(context, BlocklyActivity::class.java)
        context.startActivity(intent)
    }
    
    // Tampilkan layar informasi sederhana
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Programming") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        // Buka kembali BlocklyActivity jika user ingin membukanya lagi
                        val intent = Intent(context, BlocklyActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Code, contentDescription = "Open Blockly Editor")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Blockly Editor dibuka di aktivitas terpisah",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(context, BlocklyActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(Icons.Default.Code, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Buka Kembali Editor")
                }
            }
        }
    }
} 