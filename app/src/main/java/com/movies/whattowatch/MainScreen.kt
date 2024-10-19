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
import com.movies.whattowatch.enums.MovieCategory
import com.movies.whattowatch.enums.SortType
import com.movies.whattowatch.enums.UserMark
import com.movies.whattowatch.uielements.GenreDropdown
import com.movies.whattowatch.uielements.LoadingBox
import com.movies.whattowatch.uielements.MarkingChips
import com.movies.whattowatch.uielements.MovieListOverview
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.Orientation
import com.movies.whattowatch.uielements.SortingChip
import com.movies.whattowatch.uielements.TopBar

@Composable
fun MainScreen(navigate: (String) -> Unit, mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()
    MainScreenContent(
        viewState.isLoading,
        viewState.selectedGenre,
        viewState.shows[viewState.selectedGenre] ?: listOf(),
        when (viewState.category) {
            MovieCategory.Movie -> viewState.genres
            else -> viewState.seriesGenres
        },
        viewState.sorting,
        mainViewModel::sendEvent,
        viewState.loadMore,
        viewState.category,
        navigate
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    selectedGenre: String,
    movies: List<MovieInfo>,
    genres: List<Genre>,
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit,
    loadMore: Boolean,
    category: MovieCategory,
    navigate: (String) -> Unit
) {
    if (genres.isNotEmpty()) {
        var showFilter by remember { mutableStateOf(true) }
        TopBar(
            showFilter,
            { showFilter = !showFilter },
            if (movies.isEmpty()) "" else movies[0].posterPath,
            navigate,
            when(category){
                MovieCategory.Movie -> NavigationItem.MOVIES
                MovieCategory.Marked -> NavigationItem.MARKED
                MovieCategory.Series -> NavigationItem.SERIES
            }
        ) { innerPadding ->
            if (isLoading) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    LoadingBox()
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
                            loadMore = loadMore,
                            navigate = navigate
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

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreenContent(
        isLoading = false,
        selectedGenre = testGenre,
        movies = testMovies,
        genres = listOf(Genre(3, testGenre)),
        sortType = SortType.POPULARITY,
        eventListener = {},
        loadMore = true,
        category = MovieCategory.Movie
    ){}
}
