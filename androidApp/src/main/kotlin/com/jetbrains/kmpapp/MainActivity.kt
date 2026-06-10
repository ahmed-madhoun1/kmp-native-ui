package com.jetbrains.kmpapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * The main Android Activity entry point for the application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables drawing UI underneath the system bars (status bar, navigation bar)
        enableEdgeToEdge()
        // Sets the Jetpack Compose UI content
        setContent {
            // Renders the main App composable
            App()
        }
    }
}
