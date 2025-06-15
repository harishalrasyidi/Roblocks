package com.example.roblocks.ui.screen

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton as M3TextButton
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.roblocks.MainActivity
import com.example.roblocks.R
import com.example.roblocks.domain.viewModel.AuthViewModel
import com.example.roblocks.domain.viewModel.RoblocksViewModel
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val context = LocalContext.current
    val roblocksViewModel: RoblocksViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val authViewModel:AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pembelajar_icon),
                contentDescription = "Login Sebagai Pembelajar Icon",
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 16.dp)
                    .clip(CircleShape),
                tint = Color.Unspecified,
            )
            Text(
                text = "Masuk Sebagai",
                style = TextStyle.Default.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F88FF)
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Pembelajar",
                style = TextStyle.Default.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F88FF)
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "Login",
                style = TextStyle.Default.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C78C1)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "email",
                style = TextStyle.Default.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A65FE)
                ),
                modifier = Modifier
                    .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(0.85f)
                    .align(Alignment.Start)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Masukkan Email") },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.85f)
                    .align(Alignment.CenterHorizontally),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A65FE),
                    unfocusedBorderColor = Color(0xFF2370F1),
                    focusedTextColor = Color.DarkGray,
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Text(
                text = "password",
                style = TextStyle.Default.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A65FE)
                ),
                modifier = Modifier
                    .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(0.85f)
                    .align(Alignment.Start)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Masukkan Password") },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.85f)
                    .align(Alignment.CenterHorizontally),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A65FE),
                    unfocusedBorderColor = Color(0xFF2370F1),
                    focusedTextColor = Color.DarkGray,
                ),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    M3IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                }
            )
            Spacer(Modifier.padding(vertical = 16.dp))
            Button(
                onClick = {
                    authViewModel.login(email, password)
                },
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2370F1),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Login",
                    style = TextStyle.Default.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(Modifier.padding(vertical = 8.dp))
////            Button(
////                onClick = {
////                    scope.launch {
////                        signInWithGoogle(context, navController)
////                    }
////                },
////                shape = RoundedCornerShape(30),
////                modifier = Modifier
////                    .padding(vertical = 8.dp)
////                    .width(200.dp)
////                    .height(50.dp),
////                colors = ButtonDefaults.buttonColors(
////                    containerColor = Color(0xFF2370F1),
////                    contentColor = Color.White
////                )
////            ) {
////                Text(
////                    text = "Login With Google",
////                    style = TextStyle.Default.copy(
////                        fontSize = 24.sp,
////                        fontWeight = FontWeight.Bold,
////                        color = Color.White
////                    )
////                )
////            }
//            Spacer(Modifier.padding(vertical = 16.dp))
            M3TextButton(
                onClick = { navController.navigate("forgot_password_screen") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Lupa Password?",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A65FE),
                        textDecoration = TextDecoration.Underline
                    )
                )
            }

            Spacer(Modifier.padding(vertical = 4.dp))

            M3TextButton(
                onClick = { navController.navigate("register_screen") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Belum Punya Akun",
                    style = TextStyle.Default.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A65FE),
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                roblocksViewModel.updateUserFromAuth()
                navController.navigate("main_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                val msg = (authState as AuthViewModel.AuthState.Error).message
                if (!showErrorDialog) {
                    errorMsg = msg
                    showErrorDialog = true
                    email = ""
                    password = ""
                }
            }
            else -> {}
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            containerColor = Color(0xFF4A65FE),
            onDismissRequest = {
                showErrorDialog = false
                authViewModel.clearError()
            },
            title = { Text("Login Gagal") },
            text = { Text(errorMsg) },
            confirmButton = {
                M3TextButton(onClick = {
                    showErrorDialog = false
                    authViewModel.clearError()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

suspend fun signInWithGoogle(context: Context, navController: NavController){
    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId("43154275218-8j4ls2aqn4gbqckdarngkpt50hqpg4mp.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val googleIdTokenCredential = result.credential as GoogleIdTokenCredential
        val idToken = googleIdTokenCredential.idToken

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener{task ->
                if(task.isSuccessful) navController.navigate("home_screen")
                else Log.e("Credential", "Error navigating or not succedded login")
            }
    }catch (e: GetCredentialException){
        Log.e("Auth", "signInWithGoogle: $e")
    }
}

