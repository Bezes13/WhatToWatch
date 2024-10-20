package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.R
import com.movies.whattowatch.TestData
import com.movies.whattowatch.dataClasses.MovieInfo

@Composable
fun MovieListOverview(
    movies: List<MovieInfo>,
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit,
    loadMore: Boolean,
    navigate: (String) -> Unit,
) {

    LazyColumn {
        item {
            movies.forEach {
                MoviePosition(
                    it,
                    selectedGenre,
                    eventListener,
                    navigate
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (loadMore) {
                    Button(
                        onClick = {
                            eventListener(
                                MainViewEvent.UpdateLoadedMovies(
                                    selectedGenre
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(id = R.string.load_more_movies))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverview() {
    MovieListOverview(
        movies = TestData.testMovies,
        selectedGenre = "",
        eventListener = {},
        loadMore = false,
        {}
    )
}