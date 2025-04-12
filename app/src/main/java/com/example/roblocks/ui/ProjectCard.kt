package com.example.roblocks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.roblocks.R

@Composable
fun ProjectCard(
    title: String,
    existStatus: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00CFFF)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
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
                    Button(
                        onClick = { /* TODO: Navigate */ },
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ){
                            Text("Kamu Belum\nPunya Project!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Justify,
                                ),
                                fontWeight = FontWeight.Bold)
                        }

                    }

                    Spacer(modifier = Modifier.height(2.dp  ))

                    Button(
                        onClick = { /* TODO: Navigate */ },
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
                    }
                }
                else{

                    Button(
                        onClick = { /* TODO: Navigate */ },
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
                        onClick = { /* TODO: Navigate */ },
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
                    }
                }
                }
            }
        }
    }


