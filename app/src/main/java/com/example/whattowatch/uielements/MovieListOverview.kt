package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.dataClasses.MovieInfo

@Composable
fun MovieListOverview(
    movies: Map<String, List<MovieInfo>>,
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit,
    isLoading: Boolean,
    loadMore: Boolean
) {
    LazyColumn {
        item {
            movies[selectedGenre]?.forEach {
                MoviePosition(
                    it,
                    selectedGenre,
                    eventListener
                )
                Divider()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    if (loadMore) {
                        Button(
                            onClick = { eventListener(MainViewEvent.UpdateLoadedMovies(selectedGenre)) },
                        ) {
                            Text(text = stringResource(id = R.string.load_more_movies))
                        }
                    }

                }
            }

        }

    }
}