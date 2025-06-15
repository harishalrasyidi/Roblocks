package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.domain.viewModel.AuthViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Reset Password",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F88FF)
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Masukkan Email Anda") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2F88FF),
                unfocusedBorderColor = Color(0xFF9C78C1)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.sendPasswordResetEmail(email) },
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2370F1),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Kirim Link Reset Password",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Kembali ke Login")
        }
        if (authState is AuthViewModel.AuthState.Error) {
            val msg = (authState as AuthViewModel.AuthState.Error).message
            if (!showDialog) {
                dialogMessage = msg
                showDialog = true
            }
        }
        if (authState is AuthViewModel.AuthState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            containerColor = Color(0xFF4A65FE),
            onDismissRequest = {
                showDialog = false
                viewModel.clearError()
            },
            title = { Text("Reset Password") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
