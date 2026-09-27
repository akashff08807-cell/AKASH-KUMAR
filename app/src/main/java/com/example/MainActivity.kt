package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.TikTokDatabase
import com.example.data.repository.TikTokRepository
import com.example.ui.TikTokApp
import com.example.ui.TikTokViewModel
import com.example.ui.TikTokViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TikTokDatabase.getDatabase(applicationContext)
        val repository = TikTokRepository(
            videoDao = database.videoDao(),
            commentDao = database.commentDao()
        )
        val viewModelFactory = TikTokViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: TikTokViewModel = viewModel(factory = viewModelFactory)
                TikTokApp(viewModel = viewModel)
            }
        }
    }
}
