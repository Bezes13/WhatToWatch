package com.example.whattowatch.uielements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.R
import com.example.whattowatch.extension.getJustYear

@Composable
fun MoviePosition(
    movieInfo: MovieInfo,
    selectedGenre: String,
    saveSeen: (String, Int, Int) -> Unit,
    checkFilm: (String, Int) -> Boolean
) {
    Row {
        BasicInfo(movieInfo)
        MarkFilmButtons(movieInfo, selectedGenre, saveSeen, checkFilm)
        AsyncImage(
            modifier = Modifier
                .clickable(onClick = {})
                .align(Alignment.CenterVertically)
                .weight(1F),
            model = stringResource(R.string.image_path, movieInfo.poster_path),
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
    saveSeen: (String, Int, Int) -> Unit,
    checkFilm: (String, Int) -> Boolean
) {
    Column(
        Modifier.fillMaxHeight().weight(1F),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.End
    ) {
        Button(onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.seen) }, modifier = Modifier.fillMaxWidth()) {
            Text(if(checkFilm(selectedGenre,movieInfo.id))"Gesehen" else "Ungesehen")
        }
        Button(onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.later) }, modifier = Modifier.fillMaxWidth()) {
            Text(if(checkFilm(selectedGenre,movieInfo.id))"Später" else "UnSpäter")
        }
        Button(onClick = { saveSeen(selectedGenre, movieInfo.id, R.string.no) }, modifier = Modifier.fillMaxWidth()) {
            Text(if(checkFilm(selectedGenre,movieInfo.id))"Nein" else "Doch")
        }
    }
}

@Composable
fun RowScope.BasicInfo(movieInfo: MovieInfo) {
    Column(
        modifier = Modifier
            .weight(1f),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        if (movieInfo.user != null) {
            Text(
                text = movieInfo.user ?: "",
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
            Text(text = movieInfo.release_date.getJustYear(), textAlign = TextAlign.Center)
            Row {
                if (movieInfo.provider_name != null) {
                    movieInfo.provider_name.forEach {
                        AsyncImage(
                            model = stringResource(R.string.image_path_or, it),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Provider",
                        )
                    }
                    if (movieInfo.provider_name.isEmpty()) {
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
    MoviePosition(
        movieInfo = MovieInfo(1, "", "", 3, ",", "24456", ",", 3, 3, user = "Anna"),
        selectedGenre = "",
        saveSeen = { _, _, _ -> },
        checkFilm = {_,_-> true})
}