package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.alphaContainer
import com.movies.whattowatch.enums.MovieCategory
import com.movies.whattowatch.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showFilter: Boolean,
    changeFilter: () -> Unit,
    drawerImage: String,
    navigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigationItem = remember {
        mutableStateOf(NavigationItem.MOVIES)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.6f)) {
                Box {
                    if (drawerImage.isNotBlank()) {
                        AsyncImage(
                            modifier = Modifier.fillMaxHeight(),
                            model = stringResource(R.string.image_path, drawerImage),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentScale = ContentScale.Crop,
                            contentDescription = stringResource(id = R.string.background),
                            alpha = 0.5f
                        )
                    }
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            stringResource(id = R.string.app_name),
                            style = TextStyle(shadow = Shadow(Color.Gray, Offset(5f, 5f))),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        Divider(color = Color.Black, thickness = 3.dp)
                        NavigationItem(
                            currentNavigationItem = navigationItem,
                            navigationItem = NavigationItem.MOVIES,
                            scope = scope,
                            text = "MOVIES",
                            drawerState = drawerState,
                            navigate = { navigate(Screen.MAIN.name + "/${MovieCategory.Movie}") },
                            setActive = true
                        )
                        NavigationItem(
                            currentNavigationItem = navigationItem,
                            navigationItem = NavigationItem.SERIES,
                            scope = scope,
                            text = "SERIES",
                            drawerState = drawerState,
                            navigate = { navigate(Screen.MAIN.name + "//${MovieCategory.Series}") },
                            setActive = true
                        )
                        NavigationItem(
                            currentNavigationItem = navigationItem,
                            navigationItem = NavigationItem.SELECTION,
                            scope = scope,
                            text = "PROVIDER SELECTION",
                            drawerState = drawerState,
                            navigate = { navigate(Screen.PROVIDER.name) },
                            setActive = false
                        )
                        NavigationItem(
                            currentNavigationItem = navigationItem,
                            navigationItem = NavigationItem.SEARCH,
                            scope = scope,
                            text = "SEARCH",
                            drawerState = drawerState,
                            navigate = { navigate(Screen.SEARCH.name) },
                            setActive = false
                        )
                        NavigationItem(
                            currentNavigationItem = navigationItem,
                            navigationItem = NavigationItem.MARKED,
                            scope = scope,
                            text = "MARKED FILMS",
                            drawerState = drawerState,
                            navigate = { navigate(Screen.MAIN.name + "//${MovieCategory.Marked}") },
                            setActive = true
                        )
                    }
                }
            }

        },
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alphaContainer),
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("What to watch")
                    },
                    actions = {
                        IconButton(onClick = { changeFilter() }) {
                            Icon(
                                imageVector = if (showFilter) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Filter"
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
                                contentDescription = "Menu"
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

@Composable
private fun NavigationItem(
    currentNavigationItem: MutableState<NavigationItem>,
    navigationItem: NavigationItem,
    scope: CoroutineScope,
    text: String,
    drawerState: DrawerState,
    navigate: () -> Unit,
    setActive: Boolean
) {
    NavigationDrawerItem(
        label = {
            Text(
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(),
                text = text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
            )
        },
        shape = RectangleShape,
        selected = currentNavigationItem.value == navigationItem,
        onClick = {
            if (setActive) {
                currentNavigationItem.apply { value = navigationItem }
            }
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
                navigate()
            }
        },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = NavigationDrawerItemDefaults.colors()
                .containerColor(selected = true).value.copy(alpha = 0.6f)
        )
    )
}

enum class NavigationItem {
    SERIES, MOVIES, SEARCH, SELECTION, MARKED
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar(true, {}, "/58QT4cPJ2u2TqWZkterDq9q4yxQ.jpg", {}, {})
}