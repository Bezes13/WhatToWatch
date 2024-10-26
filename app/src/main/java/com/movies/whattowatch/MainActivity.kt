package com.movies.whattowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.movies.whattowatch.model.enums.MovieCategory
import com.movies.whattowatch.navigation.AppNavHost
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.ui.theme.WhatToWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
        setContent {
            WhatToWatchTheme {
                FirebaseApp.initializeApp(this)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavHost(
                        navController = rememberNavController(),
                        startDestination = Screen.MAIN.name + "/${MovieCategory.Movie}"
                    )
                }
            }
        }
    }
}
