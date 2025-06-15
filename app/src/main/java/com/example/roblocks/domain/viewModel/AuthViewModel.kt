package com.example.roblocks.domain.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _authState.value = if (auth.currentUser == null) {
            AuthState.Unauthenticated
        } else {
            AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email dan Password Tidak Boleh Kosong")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun startGoogleSignIn(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with your Web Client ID from Firebase Console
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleGoogleSignInResult(data: Intent?) {
        _authState.value = AuthState.Loading
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                loginWithGoogle(idToken)
            } else {
                _authState.value = AuthState.Error("Google Sign-In failed: No ID token")
            }
        } catch (e: ApiException) {
            _authState.value = AuthState.Error("Google Sign-In failed: ${e.message}")
        }
    }

    private fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email dan Password Tidak Boleh Kosong")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign-up failed")
                }
            }
    }

    fun checkPassword(password: String, passwordUlang: String): Boolean {
        if(password != passwordUlang){
            _authState.value = AuthState.Error("Password Yang di Ulang Tidak Sama!")
            return false
        }
        else
            return true
    }

    fun signout() {
        try {
            val googleSignInClient = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with your Web Client ID
                    .requestEmail()
                    .build()
            )
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                _authState.value = AuthState.Unauthenticated
            }.addOnFailureListener { e ->
                _authState.value = AuthState.Error("Logout failed: ${e.message}")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Logout failed: ${e.message}")
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email tidak boleh kosong")
            return
        }
        _authState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Error("Link reset password telah dikirim ke email Anda")
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Gagal mengirim email reset password"
                    )
                }
            }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun getCurrentUser() = auth.currentUser

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
