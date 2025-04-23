package com.example.roblocks.ui.component

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

@Composable
fun ArticleCard(
    icon: Int,
    title: String,
    description: String,
){
    Button(
        onClick = {},
        modifier = Modifier.height(100.dp).fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8412A)),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(50)),
                    tint = Color.White
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.offset(x = 6.dp, y = -4.dp)

                )
            }
            Spacer(modifier = Modifier.width(28.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.White
                ),
                modifier = Modifier.weight(1f)

            )
        }
    }
}