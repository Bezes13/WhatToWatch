package com.movies.whattowatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.movies.whattowatch.TestData.testGenre
import com.movies.whattowatch.TestData.testMovies
import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dataClasses.Provider
import com.movies.whattowatch.dto.CastDTO
import com.movies.whattowatch.dto.VideoInfoDTO
import com.movies.whattowatch.enums.SortType
import com.movies.whattowatch.enums.UserMark
import com.movies.whattowatch.uielements.GenreDropdown
import com.movies.whattowatch.uielements.MarkingChips
import com.movies.whattowatch.uielements.MovieDetailsDialog
import com.movies.whattowatch.uielements.MovieListOverview
import com.movies.whattowatch.uielements.Orientation
import com.movies.whattowatch.uielements.PersonDetailsDialog
import com.movies.whattowatch.uielements.ProviderListDialog
import com.movies.whattowatch.uielements.Search
import com.movies.whattowatch.uielements.SortingChip
import com.movies.whattowatch.uielements.TopBar

@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()
    MainScreenContent(
        viewState.isLoading,
        viewState.selectedGenre,
        viewState.shows[viewState.selectedGenre] ?: listOf(),
        when (viewState.category) {
            MovieCategory.Movie -> viewState.genres
            else -> viewState.seriesGenres
        },
        viewState.providers,
        viewState.sorting,
        viewState.dialog,
        mainViewModel::sendEvent,
        viewState.loadMore
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    selectedGenre: String,
    movies: List<MovieInfo>,
    genres: List<Genre>,
    allProviders: List<Provider>,
    sortType: SortType,
    dialog: MainViewDialog,
    eventListener: (MainViewEvent) -> Unit,
    loadMore: Boolean
) {
    if (genres.isNotEmpty()) {
        var showFilter by remember { mutableStateOf(true) }
        TopBar(
            eventListener,
            showFilter,
            { showFilter = !showFilter },
            if (movies.isEmpty()) "" else movies[0].posterPath
        ) { innerPadding ->
            when (dialog) {
                is MainViewDialog.DetailsDialog -> MovieDetailsDialog(
                    dialog.info,
                    dialog.cast,
                    dialog.video,
                    eventListener = eventListener,
                    onDismissRequest = {
                        eventListener(
                            MainViewEvent.SetDialog(MainViewDialog.None)
                        )
                    }
                )

                is MainViewDialog.PersonDetails -> PersonDetailsDialog(dialog.info, eventListener) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                is MainViewDialog.SearchDialog -> Search(
                    dialog.foundObjects,
                    dialog.loadMore,
                    dialog.page,
                    eventListener
                ) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                is MainViewDialog.ShowProviderList -> ProviderListDialog(
                    allProviders,
                    eventListener
                ) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                else -> {}
            }
            if (isLoading) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(150, 150, 150, 120))
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
            Box {
                if (movies.isNotEmpty()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxHeight(),
                        model = stringResource(R.string.image_path, movies[0].posterPath),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = stringResource(R.string.background),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f)
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (UserMark.entries.map { it.name }.contains(selectedGenre)) {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = alphaContainer))
                                .fillMaxWidth()
                        ) {
                            UserMark.entries.forEach {
                                MarkingChips(
                                    userMark = it,
                                    selectedGenre = selectedGenre,
                                    orientation = when (it) {
                                        UserMark.entries.first() -> Orientation.Left
                                        UserMark.entries.last() -> Orientation.Right
                                        else -> Orientation.Center
                                    },
                                    eventListener = eventListener
                                )

                            }
                        }
                    } else {
                        if (!showFilter) {
                            if (!UserMark.entries.map { it.name }.contains(selectedGenre)) {
                                Row(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer.copy(
                                                alpha = alphaContainer
                                            )
                                        )
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    SortingChip(sortType, SortType.POPULARITY, eventListener)
                                    SortingChip(sortType, SortType.VOTE_AVERAGE, eventListener)
                                    SortingChip(sortType, SortType.VOTE_COUNT, eventListener)
                                    SortingChip(sortType, SortType.REVENUE, eventListener)
                                }
                            }
                            GenreDropdown(genres, eventListener)
                        }
                    }

                    if (selectedGenre == "") {
                        Spacer(modifier = Modifier.size(100.dp))
                        Text(
                            text = stringResource(R.string.choose_genre),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    if (movies.isNotEmpty()) {
                        MovieListOverview(
                            movies = movies,
                            selectedGenre = selectedGenre,
                            eventListener = eventListener,
                            isLoading = isLoading,
                            loadMore = loadMore
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
}

sealed class MainViewDialog {
    data class DetailsDialog(
        val info: MovieInfo,
        val cast: List<CastDTO>,
        val video: List<VideoInfoDTO>
    ) : MainViewDialog()

    data object None : MainViewDialog()
    data class PersonDetails(val info: CastDTO) : MainViewDialog()
    data class SearchDialog(
        val foundObjects: List<MovieInfo>,
        val page: Int,
        val loadMore: Boolean
    ) : MainViewDialog()

    data object ShowProviderList : MainViewDialog()
}

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreenContent(
        isLoading = false,
        selectedGenre = testGenre,
        movies = testMovies,
        genres = listOf(Genre(3, testGenre)),
        allProviders = listOf(),
        sortType = SortType.POPULARITY,
        dialog = MainViewDialog.None,
        eventListener = {},
        loadMore = true
    )
}
