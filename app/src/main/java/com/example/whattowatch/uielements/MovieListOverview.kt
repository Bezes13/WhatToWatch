package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.dataClasses.MediaType
import com.example.whattowatch.dataClasses.MovieInfo

@Composable
fun MovieListOverview(
    movies: List<MovieInfo>,
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit,
    isLoading: Boolean,
    loadMore: Boolean
) {
    Box {
        AsyncImage(
            modifier = Modifier.fillMaxHeight(),
            model = stringResource(R.string.image_path, movies[0].posterPath),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Background",
        )
        LazyColumn {
            item {
                movies.forEach {
                    MoviePosition(
                        it,
                        selectedGenre,
                        eventListener
                    )
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
                                onClick = {
                                    eventListener(
                                        MainViewEvent.UpdateLoadedMovies(
                                            selectedGenre
                                        )
                                    )
                                },
                            ) {
                                Text(text = stringResource(id = R.string.load_more_movies))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverview() {
    val movies = listOf( MovieInfo(
        id = 533535,
        originalLanguage = "en",
        overview = "A listless Wade Wilson toils away in civilian life with his days as the morally flexible mercenary",
        popularity = 4025.483,
        posterPath = "/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
        releaseDate = "2024-07-24",
        title = "Deadpool & Wolverine",
        voteAverage = 7.74,
        voteCount = 4092,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    ), MovieInfo(
        id = 519182,
        originalLanguage = "en",
        overview = "Gru and Lucy and their girls — Margo",
        popularity = 1854.122,
        posterPath = "/wWba3TaojhK7NdycRhoQpsG0FaH.jpg",
        releaseDate = "2024 - 06 - 20",
        title = "Despicable Me 4",
        voteAverage = 7.141,
        voteCount = 1904,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    ), MovieInfo(
        id = 877817,
        originalLanguage = "en",
        overview = "Hired to cover up a high -profile crime",
        popularity = 1249.675,
        posterPath = "/vOX1Zng472PC2KnS0B9nRfM8aaZ.jpg",
        releaseDate = "2024-09-20",
        title = "Wolfs",
        voteAverage = 6.604,
        voteCount = 385,
        providerName = listOf("/2E03IAZsX4ZaUqM7tXlctEPMGWS.jpg"),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    ), MovieInfo(
        id = 957452,
        originalLanguage = "en",
        overview = "Soulmates Eric and Shelly are brutally murdered",
        popularity = 1329.937,
        posterPath = "/58QT4cPJ2u2TqWZkterDq9q4yxQ.jpg",
        releaseDate = "2024-08-21",
        title = "The Crow",
        voteAverage = 5.4,
        voteCount = 489,
        providerName = listOf(),
        isMovie = true,
        user = "",
        mediaType = MediaType.MOVIE,
        knownFor = listOf()
    ))
    MovieListOverview(movies = movies, selectedGenre = "", eventListener = {}, isLoading = false, loadMore = false)
}