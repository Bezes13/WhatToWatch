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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.whattowatch.R
import com.example.whattowatch.datas.MovieInfo
import com.example.whattowatch.extension.getJustYear

@Composable
fun MoviePosition(
    movieInfo: MovieInfo,
    selectedGenre: String,
    saveSeen: (String, Int, Int) -> Unit,
    getCast: (String, MovieInfo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        BasicInfo(movieInfo)
        Spacer(modifier = Modifier.width(8.dp))
        MarkFilmButtons(movieInfo, selectedGenre, saveSeen)
        Spacer(modifier = Modifier.width(8.dp))
        AsyncImage(
            modifier = Modifier
                .clickable(onClick = {
                    getCast(selectedGenre, movieInfo)
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
    saveSeen: (String, Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.5F),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.seen) },

            ) {
            Icon(imageVector = Icons.Filled.Favorite, contentDescription = "")
        }
        IconButton(
            onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.later) },

            ) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = "")
        }
        IconButton(
            onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.no) },

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
        if (movieInfo.user != null && movieInfo.user != "") {
            Card(colors = CardDefaults.cardColors(Color.Green)) {
                Text(
                    text = movieInfo.user ?: "",
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
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
                movieInfo = MovieInfo(1, "", "", 3, ",", "24456", ",", 3, 3, user = null),
                selectedGenre = "",
                saveSeen = { _, _, _ -> },
                getCast = { _, _ -> })
            MoviePosition(
                movieInfo = MovieInfo(1, "", "", 3, ",", "24456", ",", 3, 3, user = ""),
                selectedGenre = "",
                saveSeen = { _, _, _ -> },
                getCast = { _, _ -> })
            MoviePosition(
                movieInfo = MovieInfo(1, "", "", 3, ",", "24456", ",", 3, 3, user = "Anna"),
                selectedGenre = "",
                saveSeen = { _, _, _ -> },
                getCast = { _, _ -> })
        }
    }

}