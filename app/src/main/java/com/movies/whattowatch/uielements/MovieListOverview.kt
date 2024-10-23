package com.movies.whattowatch.uielements

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.TestData
import com.movies.whattowatch.dataClasses.MovieInfo

@Composable
fun MovieListOverview(
    movies: List<MovieInfo>,
    eventListener: (MainViewEvent) -> Unit,
    navigate: (String) -> Unit,
) {

    LazyColumn {
        item {
            movies.forEach {
                MoviePosition(
                    it,
                    eventListener,
                    navigate
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverview() {
    MovieListOverview(
        movies = TestData.testMovies,
        eventListener = {},
        {}
    )
}