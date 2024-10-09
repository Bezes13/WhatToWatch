package com.example.whattowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.whattowatch.apiRepository.ApiRepository
import com.example.whattowatch.manager.MainViewModelFactory
import com.example.whattowatch.ui.theme.WhatToWatchTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiRepository = ApiRepository(this)
        val ioDispatcher = Dispatchers.IO
        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory(apiRepository, ioDispatcher)
        }
        setContent {
            WhatToWatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(mainViewModel)
                }
            }
        }
    }
}
