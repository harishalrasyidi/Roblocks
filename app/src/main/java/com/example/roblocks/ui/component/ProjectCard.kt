package com.example.roblocks.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.data.entities.ProjectEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.viewModel.ProjectIOTViewModel
import com.example.roblocks.ui.screen.CreateProjectDialogAI
import com.example.roblocks.ui.screen.CreateProjectDialogRobotics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEmpty
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun<T: ProjectEntity> ProjectCard(
    title: String,
    existStatus: Boolean,
    jenisProyek: String,
    navController: NavController,
    projectList: List<T>
) {
    var showCreateProject by remember { mutableStateOf(false) }

    val ProjectIOTViewModel : ProjectIOTViewModel = hiltViewModel()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00CFFF)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().width(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 15.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 30.sp,
                        lineHeight = 28.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                //nanti tambah kondisi buat ngeganti teks jadi project yang udah dibuat kalau dao udah jadi
                if(existStatus == false){
                    if (projectList.isEmpty()) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.width(200.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFFFFFF
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "Kamu Belum\nPunya Project!",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            textAlign = TextAlign.Justify,
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Button(
                                onClick = { showCreateProject = true },
                                modifier = Modifier.width(200.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "Buat Projek Baru",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            textAlign = TextAlign.Justify,
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.buat_projek_baru_illustration),
                                        contentDescription = "Tambah Project Illustration",
                                        modifier = Modifier
                                            .size(17.dp)
                                            .offset(x = 3.dp, y = 1.dp)
                                    )
                                }


                                if (showCreateProject == true) {
                                    if (jenisProyek == "AI")
                                        CreateProjectDialogAI(onDismiss = {
                                            showCreateProject = false
                                        }, navController)
                                    else
                                        CreateProjectDialogRobotics(onDismiss = {
                                            showCreateProject = false
                                        }, navController)
                                }
                            }
                        }
                    }
                    else{
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            projectList.forEach {
                                Button(
                                    onClick = { print("Being Clicked") },
                                    modifier = Modifier.width(250.dp).height(IntrinsicSize.Min),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF2F88FF
                                        )
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color.White)
                                ) {
                                    //konten untuk tombol
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                Text(
                                                    "Nama Proyek: ",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        lineHeight = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Justify,
                                                    ),
                                                    fontWeight = FontWeight.Bold
                                                )


                                                Text(
                                                    it.name.toString(),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        lineHeight = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Justify,
                                                    ),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                Text(
                                                    "Tipe: ",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        lineHeight = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Justify,
                                                    ),
                                                    fontWeight = FontWeight.Bold
                                                )


                                                Text(
                                                    it.tipe,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        lineHeight = 22.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        textAlign = TextAlign.Justify,
                                                    ),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(2.dp))

                                        Image(
                                            painter = painterResource(id = R.drawable.open_proyek_illustration),
                                            contentDescription = "Buka Proyek",
                                            modifier = Modifier
                                                .size(17.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Button(
                                onClick = { showCreateProject = true },
                                modifier = Modifier.width(200.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "Buat Projek Baru",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            textAlign = TextAlign.Justify,
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.buat_projek_baru_illustration),
                                        contentDescription = "Tambah Project Illustration",
                                        modifier = Modifier
                                            .size(17.dp)
                                            .offset(x = 3.dp, y = 1.dp)
                                    )
                                }


                                if (showCreateProject == true) {
                                    if (jenisProyek == "AI")
                                        CreateProjectDialogAI(onDismiss = {
                                            showCreateProject = false
                                        }, navController)
                                    else
                                        CreateProjectDialogRobotics(onDismiss = {
                                            showCreateProject = false
                                        }, navController)
                                }
                            }

                            Button(
                                onClick = { showCreateProject = true },
                                modifier = Modifier.width(200.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4C543)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        "Edit Projek",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            textAlign = TextAlign.Justify,
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.edit_proyek_illustration),
                                        contentDescription = "Edit Project Illustration",
                                        modifier = Modifier
                                            .size(17.dp)
                                            .offset(x = 3.dp, y = 1.dp)
                                    )
                                }
        //                        if(showCreateProject == true) {
        //                            if(jenisProyek == "AI")
        //                                CreateProjectDialogAI(onDismiss = { showCreateProject = false }, navController = navController)
        //                            else
        //                                CreateProjectDialogRobotics(onDismiss = { showCreateProject = false}, navController = navController)
        //                        }
                                    }
                        }
                    }
                }
                else{
                    Button(
                        onClick = { },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F88FF)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ){
                            Text("<Project Title>" ,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Justify,
                                ),
                                fontWeight = FontWeight.Bold)
                        }

                    }

                    Spacer(modifier = Modifier.height(2.dp  ))

                    Button(
                        onClick = { showCreateProject = true },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                "Buat Projek Baru",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Justify,
                                ),
                                fontWeight = FontWeight.Bold
                            )

                            Image(
                                painter = painterResource(id = R.drawable.buat_projek_baru_illustration),
                                contentDescription = "Tambah Project Illustration",
                                modifier = Modifier
                                    .size(17.dp)
                                    .offset(x = 3.dp, y = 1.dp)
                            )
                        }
                        if(showCreateProject == true) {
                            if(jenisProyek == "AI")
                                CreateProjectDialogAI(onDismiss = { showCreateProject = false }, navController = navController)
                            else
                                CreateProjectDialogRobotics(onDismiss = { showCreateProject = false}, navController = navController)
                        }
                    }

                    Button(
                        onClick = { showCreateProject = true },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4C543)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                "Buat Projek Baru",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Justify,
                                ),
                                fontWeight = FontWeight.Bold
                            )

                            Image(
                                painter = painterResource(id = R.drawable.edit_proyek_illustration),
                                contentDescription = "Tambah Project Illustration",
                                modifier = Modifier
                                    .size(17.dp)
                                    .offset(x = 3.dp, y = 1.dp)
                            )
                        }
//                        if(showCreateProject == true) {
//                            if(jenisProyek == "AI")
//                                CreateProjectDialogAI(onDismiss = { showCreateProject = false }, navController = navController)
//                            else
//                                CreateProjectDialogRobotics(onDismiss = { showCreateProject = false}, navController = navController)
//                        }
                    }
                }
                }
            }
        }
    }


