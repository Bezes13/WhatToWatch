package com.example.whattowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.whattowatch.managers.MainViewModelFactory
import com.example.whattowatch.managers.SharedPreferencesManager
import com.example.whattowatch.repository.ApiRepository
import com.example.whattowatch.ui.theme.WhatToWatchTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val sharedPreferencesManager = SharedPreferencesManager(this)
        val apiRepository = ApiRepository(this)
        val ioDispatcher = Dispatchers.IO
        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory(apiRepository, sharedPreferencesManager, ioDispatcher)
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
