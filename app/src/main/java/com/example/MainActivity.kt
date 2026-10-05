package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MoneyTycoonTheme
import com.example.viewmodel.TycoonViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TycoonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyTycoonTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
