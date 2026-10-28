package com.linaresbar.appdenuncias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.linaresbar.appdenuncias.ui.screens.MainScreen
import com.linaresbar.appdenuncias.ui.theme.AppDenunciasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDenunciasTheme {
                MainScreen()
            }
        }
    }
}
