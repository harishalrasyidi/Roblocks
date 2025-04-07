package com.example.roblocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.roblocks.ui.MainScreen
import com.example.roblocks.ui.theme.RoblocksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoblocksTheme {
                MainScreen()
            }
        }
    }
}
