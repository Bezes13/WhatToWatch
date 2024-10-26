package com.movies.whattowatch.uielements

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.R
import com.movies.whattowatch.TestData.movie1
import com.movies.whattowatch.TestData.movie2
import com.movies.whattowatch.TestData.movie3
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.details.MyCard
import com.movies.whattowatch.details.Providers
import com.movies.whattowatch.enums.UserMark
import com.movies.whattowatch.extension.getJustYear
import com.movies.whattowatch.navigation.Screen


@Composable
fun MoviePosition(
    movieInfo: MovieInfo,
    eventListener: (MainViewEvent) -> Unit,
    navigate: (String) -> Unit,
) {
    var isMarked by remember { mutableStateOf(false) }

    AnimatedVisibility(!isMarked) {
        MyCard(modifier = Modifier.padding(5.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) {
                BasicInfo(movieInfo)
                Spacer(modifier = Modifier.width(8.dp))
                MarkFilmButtons(movieInfo, { isMarked = true },eventListener)
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    modifier = Modifier
                        .clickable(onClick = {
                            navigate(Screen.DETAILS.name + "/${movieInfo.id}/${movieInfo.isMovie}")
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
    }


}

@Composable
fun RowScope.MarkFilmButtons(
    movieInfo: MovieInfo,
    onClick: () -> Unit,
    eventListener: (MainViewEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.5F),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.End
    ) {
        UserMark.entries.forEach {
            IconButton(
                onClick = {
                    onClick()
                    eventListener(MainViewEvent.MarkFilmAs(movieInfo, it))
                }
            ) {
                Icon(imageVector = it.icon, contentDescription = "")
            }
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

            Providers(movieInfo = movieInfo)
        }
    }
}


@Preview
@Composable
fun PreviewPosition() {
    LazyColumn {
        item {
            MoviePosition(
                movieInfo = movie1,
                eventListener = {}){}
            MoviePosition(
                movieInfo = movie2,
                eventListener = {}){}
            MoviePosition(
                movieInfo = movie3,
                eventListener = {}){}
        }
    }

}