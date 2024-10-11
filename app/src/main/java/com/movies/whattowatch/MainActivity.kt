package com.movies.whattowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.movies.whattowatch.apiRepository.ApiRepository
import com.movies.whattowatch.manager.MainViewModelFactory
import com.movies.whattowatch.ui.theme.WhatToWatchTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiRepository = ApiRepository(this)
        val ioDispatcher = Dispatchers.IO
        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory(apiRepository, ioDispatcher)
        }
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
        setContent {
            WhatToWatchTheme {
                FirebaseApp.initializeApp(this)
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