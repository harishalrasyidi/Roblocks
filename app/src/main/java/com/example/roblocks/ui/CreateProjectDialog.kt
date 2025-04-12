package com.example.roblocks.ui

import android.graphics.Paint.Align
import android.provider.CalendarContract.Colors
import android.renderscript.ScriptGroup.Input
import android.view.RoundedCorner
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.roblocks.R


@Composable
fun CreateProjectDialog(onDismiss: () -> Unit) {
    val namaProyek = remember { mutableStateOf("") }

    AlertDialog(
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
                            text = "Nama Proyek",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 12.sp,
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = namaProyek.value,
                            onValueChange = { namaProyek.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Masukkan Nama Proyek") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Handle the confirm button click, like saving the project
                onDismiss() // Close the dialog after confirmation
            }) {
                Text("Buat Proyek")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
