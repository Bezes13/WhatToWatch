package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.whattowatch.MainViewDialog
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(eventListener: (MainViewEvent) -> Unit, content: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isMovie = remember {
        mutableStateOf(true)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(id = R.string.app_name), fontSize = 30.sp)
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Movies") },
                    selected = isMovie.value,
                    onClick = {
                        isMovie.apply { value = true }
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                            eventListener(MainViewEvent.ChangeIsMovie(true))
                        }
                    }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Series") },
                    selected = !isMovie.value,
                    onClick = {
                        scope.launch {
                            isMovie.apply { value = false }
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                            eventListener(MainViewEvent.ChangeIsMovie(false))
                        }
                    }
                )
                Divider()
            }

        },
    ) {
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
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu",

                                )
                        }
                    }
                )
            },
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}