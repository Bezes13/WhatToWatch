package com.example.whattowatch.uielements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.TestData.movie1
import com.example.whattowatch.TestData.movie2
import com.example.whattowatch.TestData.movie3
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.enums.UserMark
import com.example.whattowatch.extension.getJustYear


@Composable
fun MoviePosition(
    movieInfo: MovieInfo,
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit,
    getCast: (MovieInfo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        BasicInfo(movieInfo)
        Spacer(modifier = Modifier.width(8.dp))
        MarkFilmButtons(movieInfo, selectedGenre, eventListener)
        Spacer(modifier = Modifier.width(8.dp))
        AsyncImage(
            modifier = Modifier
                .clickable(onClick = {
                    getCast(movieInfo)
                })
                .align(Alignment.CenterVertically)
                .weight(0.5F),
            model = stringResource(R.string.image_path, movieInfo.posterPath),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = movieInfo.title,
        )
    }

}

@Composable
fun RowScope.MarkFilmButtons(
    movieInfo: MovieInfo,
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.5F),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = { eventListener(MainViewEvent.MarkFilmAs(selectedGenre,movieInfo.id, UserMark.SEEN))},

            ) {
            Icon(imageVector = Icons.Filled.Favorite, contentDescription = "")
        }
        IconButton(
            onClick = { eventListener(MainViewEvent.MarkFilmAs(selectedGenre,movieInfo.id, UserMark.LATER))},

            ) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = "")
        }
        IconButton(
            onClick = { eventListener(MainViewEvent.MarkFilmAs(selectedGenre,movieInfo.id, UserMark.NO)) },

            ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "")
        }
    }
}

@Composable
fun RowScope.BasicInfo(movieInfo: MovieInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1F)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = movieInfo.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Text(text = movieInfo.releaseDate.getJustYear(), textAlign = TextAlign.Center)

            Row {
                if (movieInfo.providerName != null) {
                    movieInfo.providerName.forEach {
                        AsyncImage(
                            model = stringResource(R.string.image_path_or, it),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Provider",
                        )
                    }
                    if (movieInfo.providerName.isEmpty()) {
                        Image(
                            modifier = Modifier.size(50.dp),
                            painter = painterResource(id = R.drawable.na),
                            contentDescription = stringResource(id = R.string.not_available)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewPosition() {
    LazyColumn() {
        item {
            MoviePosition(
                movieInfo = movie1,
                selectedGenre = "",
                eventListener = {},
                getCast = {  })
            MoviePosition(
                movieInfo = movie2,
                selectedGenre = "",
                eventListener = {},
                getCast = {  })
            MoviePosition(
                movieInfo = movie3,
                selectedGenre = "",
                eventListener = {},
                getCast = {  })
        }
    }

}