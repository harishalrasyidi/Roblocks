package com.example.roblocks.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.R
import com.example.roblocks.domain.viewModel.AuthViewModel
import com.example.roblocks.ui.BottomNavBar

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val selectedIndex = remember { mutableStateOf(4) }
    val user = viewModel.getCurrentUser()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("main_screen")
                        1 -> navController.navigate("artificial_intelligence_screen")
                        2 -> navController.navigate("robotics_screen")
                        3 -> navController.navigate("learn_screen")
                        4 -> {}
                    }
                }
            )
        },
        containerColor = Color(0xFFF9F9FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text("Halaman,", color = Color.Gray)
            Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.width(500.dp).height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2FCEEA))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(.60f).align(Alignment.CenterHorizontally).padding(top = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user_icon),
                            contentDescription = "Profile Icon",
                            modifier = Modifier.size(120.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileField("Nama", user?.displayName ?: "Not set")
                    ProfileField("Email", user?.email ?: "Not set")
                    ProfileField("User ID", user?.uid ?: "Not set")
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = { 
                    viewModel.signout()
                    navController.navigate("login_screen") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardBackspace,
                    contentDescription = "Logout"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Logout",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Composable
private fun ProfileField(label: String, value: String) {
    Text(
        textAlign = TextAlign.Start,
        text = label,
        style = TextStyle.Default.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4A65FE)
        ),
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp)
            .fillMaxWidth(0.90f)
    )
    OutlinedTextField(
        readOnly = true,
        value = value,
        onValueChange = { value },
        textStyle = TextStyle(fontSize = 14.sp),
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4A65FE),
            unfocusedBorderColor = Color(0xFF2370F1),
            focusedTextColor = Color.Gray,
            unfocusedTextColor = Color.Gray
        ),
        shape = RoundedCornerShape(20.dp),
    )
}
