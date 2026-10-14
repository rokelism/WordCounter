package com.example.wordcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.wordcounter.ui.WordCounterScreen
import com.example.wordcounter.ui.WordCounterViewModel
import com.example.wordcounter.ui.theme.WordCounterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WordCounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordCounterTheme {
                WordCounterScreen(viewModel = viewModel)
            }
        }
    }
}
