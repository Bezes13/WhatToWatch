package com.movies.whattowatch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movies.whattowatch.TestData.testGenre
import com.movies.whattowatch.TestData.testMovies
import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.enums.MovieCategory
import com.movies.whattowatch.enums.SortType
import com.movies.whattowatch.uielements.BackgroundImage
import com.movies.whattowatch.uielements.LoadingBox
import com.movies.whattowatch.uielements.MarkedFilmsHeader
import com.movies.whattowatch.uielements.MovieFilter
import com.movies.whattowatch.uielements.MovieListOverview
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar

@Composable
fun MainScreen(navigate: (String) -> Unit, mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()

    MainScreenContent(
        viewState.isLoading,
        viewState.selectedGenre,
        viewState.shows,
        when (viewState.category) {
            MovieCategory.Movie -> viewState.genres
            else -> viewState.seriesGenres
        },
        viewState.sorting,
        mainViewModel::sendEvent,
        viewState.category,
        navigate
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    selectedGenre: List<Genre>,
    movies: List<MovieInfo>,
    genres: List<Genre>,
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit,
    category: MovieCategory,
    navigate: (String) -> Unit
) {
    if (genres.isNotEmpty()) {
        var showFilter by remember { mutableStateOf(false) }
        TopBar(
            showFilter,
            { showFilter = !showFilter },
            if (movies.isEmpty()) "" else movies[0].posterPath,
            navigate,
            when (category) {
                MovieCategory.Movie -> NavigationItem.MOVIES
                MovieCategory.Marked -> NavigationItem.MARKED
                MovieCategory.Series -> NavigationItem.SERIES
            }
        ) { innerPadding ->
            if (!isLoading) {
                Box {
                    if (movies.isNotEmpty()) {
                        BackgroundImage(movies[0].posterPath)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(1f)
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (category == MovieCategory.Marked) {
                            MarkedFilmsHeader(selectedGenre[0].name, eventListener)
                        } else {
                            AnimatedVisibility(
                                showFilter
                            ) {
                                MovieFilter(sortType, eventListener, genres, selectedGenre)
                            }
                        }

                        if (movies.isNotEmpty()) {
                            MovieListOverview(
                                movies = movies,
                                eventListener = eventListener,
                                navigate = navigate
                            )
                        }
                    }
                }
            } else {
                LoadingBox()
            }
        }
    }
}

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreenContent(
        isLoading = false,
        selectedGenre = listOf(),
        movies = testMovies,
        genres = listOf(Genre(3, testGenre)),
        sortType = SortType.POPULARITY,
        eventListener = {},
        category = MovieCategory.Movie
    ) {}
}
