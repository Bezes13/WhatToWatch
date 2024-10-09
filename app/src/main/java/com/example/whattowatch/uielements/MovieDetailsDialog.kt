package com.example.whattowatch.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.TestData.movie3
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dto.CastDTO
import com.example.whattowatch.dto.VideoInfoDTO
import com.example.whattowatch.extension.getJustYear
import eu.wewox.textflow.TextFlow
import eu.wewox.textflow.TextFlowObstacleAlignment

@Composable
fun MovieDetailsDialog(
    info: MovieInfo,
    cast: List<CastDTO>,
    video: List<VideoInfoDTO>,
    onDismissRequest: () -> Unit,
    eventListener: (MainViewEvent) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            if (isExpanded) {
                AsyncImage(
                    model = stringResource(R.string.image_path, info.posterPath),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = info.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = { isExpanded = false })
                )
            } else {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Header(info)
                    LazyColumn(
                        Modifier
                            .weight(1F)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            MovieOverview(info) { isExpanded = true }
                            Providers(info)
                            CastInfo(cast, eventListener)
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                if (video.isNotEmpty()) {
                                    video.filter { info -> info.site == "YouTube" }.forEach {
                                        VideoPlayer(it.key)
                                    }
                                }
                            }

                        }
                    }
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
fun Providers(movieInfo: MovieInfo) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(5.dp)) {
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

@Composable
private fun CastInfo(
    cast: List<CastDTO>,
    eventListener: (MainViewEvent) -> Unit
) {
    Row(
        Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cast.filterIndexed { index, _ -> index % 2 == 0 }.forEach {
                AsyncImage(
                    model = stringResource(R.string.image_path, it.profile_path),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = it.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = { eventListener(MainViewEvent.FetchCredits(it)) })
                )
                Text(text = it.name)
                Divider()
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cast.filterIndexed { index, _ -> index % 2 == 1 }.forEach {
                AsyncImage(
                    model = stringResource(
                        R.string.image_path, it.profile_path
                    ),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = it.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = { eventListener(MainViewEvent.FetchCredits(it)) })
                )
                Text(text = it.name)
                Divider()
            }
        }
    }
}

@Composable
private fun MovieOverview(
    info: MovieInfo,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(R.string.bewertung)
            )
            Text(text = "${info.voteAverage} by ${info.voteCount}")
        }
    }

    TextFlow(text = info.overview,
        obstacleAlignment = TextFlowObstacleAlignment.TopStart,
        obstacleContent = {
            AsyncImage(
                model = stringResource(
                    R.string.image_path, info.posterPath
                ),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = info.title,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(10.dp)
            )
        })

}


@Composable
private fun Header(info: MovieInfo) {
    Row {
        Text(buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = 30.sp, textDecoration = TextDecoration.Underline
                )
            ) {
                append(info.title)
            }
            withStyle(
                style = SpanStyle(
                    fontSize = 10.sp
                )
            ) {
                append(info.releaseDate.getJustYear())
            }
        })
    }
    Divider()
}

@Preview
@Composable
fun MovieDialogPreview() {
    MovieDetailsDialog(movie3, listOf(), listOf(), {}, {})
}