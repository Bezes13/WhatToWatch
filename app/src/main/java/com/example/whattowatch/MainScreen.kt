package com.example.whattowatch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whattowatch.dataObjects.MovieInfo
import com.example.whattowatch.dto.SingleGenreDTO
import com.example.whattowatch.uielements.GenreDropdown
import com.example.whattowatch.uielements.MovieDetailsDialog
import com.example.whattowatch.uielements.MovieListOverview
import com.example.whattowatch.uielements.TextFieldDialog
import com.example.whattowatch.uielements.TopBar


@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()
    if (mainViewModel.readName(R.string.user_name) == "") {
        mainViewModel.sendEvent(MainViewEvent.SetDialog(MainViewDialog.EnterName))
    }
    MainScreenContent(
        viewState.isLoading,
        viewState.selectedGenre,
        viewState.movies,
        viewState.genres,
        mainViewModel.markFilmAs,
        mainViewModel::getMovies,
        mainViewModel::getCustomList,
        mainViewModel::saveSharedList,
        mainViewModel::saveName,
        viewState.dialog,
        mainViewModel::changeLoadedMovies,
        mainViewModel::sendEvent,
        mainViewModel::getCast
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    selectedGenre: String,
    movies: Map<String, List<MovieInfo>>,
    genres: List<SingleGenreDTO>,
    additionalGenres: List<String>,
    getMovies: (SingleGenreDTO) -> Unit,
    getCustomList: (String) -> Unit,
    saveSeen: (String, Int, Int) -> Unit,
    saveName: (String, Int) -> Unit,
    dialog: MainViewDialog,
    changeLoadedMovies: (String) -> Unit,
    eventListener: (MainViewEvent) -> Unit,
    getCast: (String, MovieInfo) -> Unit,
) {
    if (genres.isNotEmpty()) {
        TopBar(eventListener) { innerPadding ->
            
            when (dialog) {
                is MainViewDialog.ShareWithFriend -> TextFieldDialog(
                    title = stringResource(R.string.sharing_is_caring),
                    text = stringResource(R.string.enter_friend_name),
                    saveID = R.string.friend_name,
                    onDismissRequest = { eventListener(MainViewEvent.SetDialog(MainViewDialog.None)) },
                    saveName = saveName
                )

                is MainViewDialog.EnterName -> TextFieldDialog(
                    title = stringResource(id = R.string.enter_name),
                    text = stringResource(id = R.string.enter_name_text),
                    saveID = R.string.user_name,
                    onDismissRequest = { eventListener(MainViewEvent.SetDialog(MainViewDialog.None)) },
                    saveName = saveName
                )

                is MainViewDialog.DetailsDialog -> MovieDetailsDialog(dialog.info) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                else -> {}
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(innerPadding)
            ) {

                GenreDropdown(genres, getMovies, additionalGenres, getCustomList, eventListener)
                
                if (selectedGenre == "") {
                    Spacer(modifier = Modifier.size(100.dp))
                    Text(
                        text = stringResource(R.string.choose_genre),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                if (movies[selectedGenre] != null && (movies[selectedGenre]
                        ?: listOf()).isNotEmpty()
                ) {
                    MovieListOverview(
                        movies = movies,
                        selectedGenre = selectedGenre,
                        saveSeen = saveSeen,
                        getCast = getCast,
                        isLoading = isLoading,
                        changeLoadedMovies = changeLoadedMovies
                    )
                } else {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .width(64.dp)
                                    .align(Alignment.Center),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class MainViewDialog() {
    data class DetailsDialog(val info: MovieInfo) : MainViewDialog()
    data object None : MainViewDialog()
    data object ShareWithFriend : MainViewDialog()
    data object EnterName : MainViewDialog()
}

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreenContent(
        isLoading = false,
        selectedGenre = "mappa",
        movies = mapOf(
            Pair(
                "mappa", listOf(
                    MovieInfo(
                        231,
                        "Englsich",
                        "Toller Film",
                        12,
                        "pasdl",
                        "22.02.2022",
                        "Marsianer",
                        123,
                        123,
                        listOf("Netflix"),
                        "Abba"
                    ),
                    MovieInfo(
                        231,
                        "Englsich",
                        "Toller Film",
                        12,
                        "pasdl",
                        "22.02.2022",
                        "Marsianer",
                        123,
                        123,
                        listOf("Netflix")
                    ),
                    MovieInfo(
                        231,
                        "Englsich",
                        "Toller Film",
                        12,
                        "pasdl",
                        "22.02.2022",
                        "Marsianer",
                        123,
                        123,
                        listOf("Netflix")
                    ),
                )
            )
        ),
        genres = listOf(SingleGenreDTO(3, "mappa")),
        additionalGenres = listOf("Gesehen"),
        getMovies = {},
        getCustomList = {},
        saveSeen = { _, _, _ -> },
        saveName = { _, _ -> },
        dialog = MainViewDialog.None,
        changeLoadedMovies = {},
        eventListener = {},
        getCast = { _, _ -> },
    )
}