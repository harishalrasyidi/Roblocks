package com.example.roblocks.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.roblocks.R

@Composable
fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        Icons.Filled.Home,
        R.drawable.ic_ai,
        R.drawable.ic_robot,
        Icons.Filled.School,
        Icons.Filled.Person
    )

    BottomAppBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items.forEachIndexed { index, icon ->
            val selected = selectedIndex == index
            val backgroundColor by animateColorAsState(
                targetValue = if (selected) Color(0xFFFAD8FF) else Color.Transparent,
                label = ""
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable { onItemSelected(index) }
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (icon is ImageVector) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (selected) Color(0xFFCC4BC2) else Color.Black
                    )
                } else {
                    Image(
                        painter = painterResource(id = icon as Int),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}