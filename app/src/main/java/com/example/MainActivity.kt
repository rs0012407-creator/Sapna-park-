package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.navigation.AppNavigation
import com.example.ui.SapanaParkViewModel
import com.example.ui.theme.SapanaParkTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SapanaParkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SapanaParkTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}

