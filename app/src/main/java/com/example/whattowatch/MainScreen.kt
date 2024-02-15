package com.example.whattowatch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.uielements.MoviePosition
import com.example.whattowatch.uielements.ShareFriendDialog
import com.example.whattowatch.uielements.genreDropdown


@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()

    MainScreenContent(
        viewState.movies,
        viewState.genres,
        mainViewModel.markFilmAs,
        mainViewModel::getMovies,
        mainViewModel::getCustomList,
        mainViewModel::saveSharedList,
        mainViewModel::saveName,
        mainViewModel::readName,
        viewState.dialog,
        mainViewModel::sendEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MainScreenContent(
    movies: Map<String, List<MovieInfo>>,
    genres: List<Genre>,
    additionalGenres: List<String>,
    getMovies: (Genre) -> Unit,
    getCustomList: (String) -> Unit,
    saveSeen: (String, Int, Int) -> Unit,
    saveName: (String, Int) -> Unit,
    readName: (Int) -> String,
    dialog: MainViewDialog,
    eventListener: (MainviewEvent) -> Unit
) {
    if (genres.isNotEmpty()) {
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
                        IconButton(onClick = { eventListener(MainviewEvent.SetDialog(MainViewDialog.ShareWithFriend))}) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share with a Friend"
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            
            when(dialog){
                is MainViewDialog.ShareWithFriend -> ShareFriendDialog(
                    onDismissRequest = { eventListener(MainviewEvent.SetDialog(MainViewDialog.None))},
                    saveName = saveName,

                )
                else -> {}
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(innerPadding)
            ) {

                val selectedGenre =
                    genreDropdown(genres, getMovies, additionalGenres, getCustomList)
                if (selectedGenre == "") {
                    Spacer(modifier = Modifier.size(100.dp))
                    Text(
                        text = "Wähle ein Genre aus",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                LazyColumn {
                    item {
                        movies[selectedGenre]?.forEach {
                            MoviePosition(it, selectedGenre, saveSeen)
                            Divider()
                        }
                    }
                }


                var hide by remember { mutableStateOf(false) }
                if (readName(R.string.user_name) == "" || hide) {
                    var newTodoText by remember { mutableStateOf("") }
                    val keyboardController = LocalSoftwareKeyboardController.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        OutlinedTextField(
                            value = newTodoText,
                            onValueChange = { newTodoText = it },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .align(Alignment.CenterVertically),
                            textStyle = TextStyle(
                                color = Color.Black
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                        SmallFloatingActionButton(
                            onClick = {
                                saveName(newTodoText, R.string.user_name)
                                keyboardController?.hide()
                                hide = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Filled.Create, "Save Name")
                        }
                    }
                }

            }
        }
    }
}

sealed class MainViewDialog(){
    data object None:MainViewDialog()
    data object ShareWithFriend:MainViewDialog()
}
@Composable
@Preview
fun PreviewMainScreen() {
    MainScreen()
}