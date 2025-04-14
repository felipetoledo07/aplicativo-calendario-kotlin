package com.example.calendario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.calendario.ui.navigation.AppNavigation
import com.example.calendario.ui.theme.CalendarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarioTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
