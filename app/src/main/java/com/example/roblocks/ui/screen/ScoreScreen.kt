package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScoreScreen(score: Int, total: Int, onBackToModules: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Skor Anda", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("$score dari $total", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBackToModules) {
            Text("Kembali ke Modul")
        }
    }
}