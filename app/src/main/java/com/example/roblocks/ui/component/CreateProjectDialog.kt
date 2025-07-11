package com.example.roblocks.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.data.entities.ProjectEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.viewModel.ProjectAIViewModel
import com.example.roblocks.domain.viewModel.ProjectIOTViewModel
import com.example.roblocks.domain.viewModel.RoblocksViewModel
import java.util.UUID

@Composable
fun InsertNamaProyek(
    title: String,
    route: String,
    onDismiss: () -> Unit,
    navController: NavController,
    projectType: String
) {
    val namaProyek = remember { mutableStateOf("") }
    val deskripsiProyek = remember { mutableStateOf("") }
    val projectIOTViewModel : ProjectIOTViewModel = hiltViewModel()
    val projectAIViewModel : ProjectAIViewModel = hiltViewModel()

    val roblocksViewModel : RoblocksViewModel = hiltViewModel()

    AlertDialog(
        containerColor = Color(0xFFA199FF),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF55A8)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(10.dp).fillMaxWidth().wrapContentHeight()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nama Proyek",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 16.sp,
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = namaProyek.value,
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Black,
                                fontSize = 16.sp,
                            ),
                            onValueChange = { namaProyek.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Masukkan Nama Proyek") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Magenta,
                                unfocusedIndicatorColor = Color.Magenta,
                            ),
                            shape = RoundedCornerShape(8.dp),
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Deskripsi Proyek",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 16.sp,
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = deskripsiProyek.value,
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Black,
                                fontSize = 16.sp,
                            ),
                            onValueChange = { deskripsiProyek.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Masukkan Deskripsi Proyek") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Magenta,
                                unfocusedIndicatorColor = Color.Magenta,
                            ),
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if(projectType == "IOT") {
                    if (namaProyek.value.isNotBlank()) {
                        projectIOTViewModel.saveProject(namaProyek.value, title)
                        onDismiss()
                    }
                }
                else{
                    if (namaProyek.value.isNotBlank()) {
                        val newProjectAI = ProjectAIEntity(
                            id = UUID.randomUUID().toString(),
                            name = namaProyek.value,
                            tipe = title,
                            file_source_proyek_AI = "", // path to AI model
                            created_at = System.currentTimeMillis(),
                            updated_at = System.currentTimeMillis(),
                            workspace_xml = "", // Blockly XML
                            id_siswa = "current_user_id"
                        )
                        roblocksViewModel.saveProject(newProjectAI)
                        navController.navigate("ml_image")
                        onDismiss()
                    }
                }
            }) {
                Text(
                    "Buat Proyek",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                )
            }
        }
    )
}


@Composable
fun CardJenisProyek(
    title: String,
    icon: Int,
    description: String,
    route: String,
    navController: NavController,
    projectType: String
) {
    var inputNamaProyek by remember { mutableStateOf(false) }
    var showNoExampleAlert by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFA199FF),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        tint = Color.White,
                    )

                }
            }
            if (showNoExampleAlert) {
                AlertDialog(
                    onDismissRequest = { showNoExampleAlert = false },
                    title = { Text("Informasi") },
                    text = { Text("Oops, belum ada contoh proyek!") },
                    confirmButton = {
                        TextButton(onClick = { showNoExampleAlert = false }) {
                            Text("OK")
                        }
                    },
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2F88FF),
                    textContentColor = Color.Black
                )
            }
            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                    Button(
                        onClick = {
                            showNoExampleAlert = true
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF55A8)),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Lihat Contoh!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.weight(0.3f))

                    Button(
                        onClick = { inputNamaProyek = true },
                        colors = ButtonDefaults.buttonColors(Color(0xFF2FCEEA)),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Buat Proyek",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    }
                if(inputNamaProyek){
                    InsertNamaProyek(title, route, onDismiss = {inputNamaProyek = false}, navController, projectType)
                }
            }
        }
    }

}

