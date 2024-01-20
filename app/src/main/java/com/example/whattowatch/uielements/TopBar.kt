package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.whattowatch.MainViewDialog
import com.example.whattowatch.MainViewEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar (eventListener: (MainViewEvent) -> Unit, content: @Composable (PaddingValues) -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("What to watch")
                },
                actions = {
                    IconButton(onClick = { eventListener(MainViewEvent.SetDialog(MainViewDialog.ShareWithFriend)) }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share with a Friend"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}