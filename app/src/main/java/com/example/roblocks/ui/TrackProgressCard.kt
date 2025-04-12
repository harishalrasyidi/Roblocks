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
import androidx.compose.ui.unit.*
import com.example.roblocks.R

@Composable
fun TrackProgressCard() {
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
            Image(
                painter = painterResource(id = R.drawable.track_progress_ilustration),
                contentDescription = "Robot Image",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(100.dp)
                    .offset(x = (-10).dp, y = (-20).dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = "Track\nYour Progress",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 30.sp,
                        lineHeight = 28.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Navigate */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6FFF))
                ) {
                    Text("CHECKNOW",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontSize = 20.sp,
                            lineHeight = 28.sp
                        ),
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
