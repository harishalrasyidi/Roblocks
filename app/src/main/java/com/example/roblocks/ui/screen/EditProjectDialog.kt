package com.example.roblocks.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.data.entities.ProjectEntity
import com.example.roblocks.domain.viewModel.ProjectAIViewModel
import com.example.roblocks.domain.viewModel.ProjectIOTViewModel

@Composable
fun EditProjectDialog(title: String, onDismiss: () -> Unit, navController: NavController, tipe: String) {
    val namaProyek = remember { mutableStateOf("") }
    val deskripsiProyek = remember { mutableStateOf("") }
    val projectIOTViewModel: ProjectIOTViewModel = hiltViewModel()
    val projectAIViewModel: ProjectAIViewModel = hiltViewModel()

    AlertDialog(
        containerColor = Color(0xFF4A65FE),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Proyek $title Yang Anda Punya",
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

//                        val projectList: List<ProjectEntity>

                        val state by if(tipe == "IOT"){
                            projectIOTViewModel.getAllProject().collectAsState(initial = null)
                        }else {
                            projectAIViewModel.getAllProject().collectAsState(initial = null)
                        }

                        state?.let { projectList ->
                            if(projectList.isNotEmpty()){
                                projectList.forEach {
                                    CardEditProyek(it.name, it.tipe, onDismiss, it.updated_at, navController, it.id)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                            else{
                                LaunchedEffect(Unit) {
                                    onDismiss()
                                }
                            }
                        }
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



@SuppressLint("SuspiciousIndentation")
@Composable
fun CardEditProyek(
    title: String,
    tipe: String,
    onDismiss: () -> Unit,
    lastUpdate: Long,
    navController: NavController,
    id: String
) {
    var inputNamaProyek by remember { mutableStateOf(false) }
    val aiViewModel: ProjectAIViewModel = hiltViewModel()
    val robotViewModel: ProjectIOTViewModel = hiltViewModel()

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
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
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
                            text = tipe,
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
                        if(tipe == "")
                            Icon(
                                painter = painterResource(R.drawable.open_proyek_illustration),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                tint = Color.White,
                            )

                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        colors = ButtonDefaults.buttonColors(Color(0xFFF4C543)),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Edit Proyek",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.weight(0.3f))

                    Button(
                        onClick = {
                            if(tipe == "IOT"){
                                robotViewModel.deleteProjectByID(id)

                            }
                            else{
                                aiViewModel.deleteProjectByID(id)
                            }


                                  },
                        colors = ButtonDefaults.buttonColors(Color(0xFFC8412A)),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Delete Proyek",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    }
                }
            }
        }
}