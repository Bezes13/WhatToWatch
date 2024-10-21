package com.movies.whattowatch.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dto.CastDTO
import com.movies.whattowatch.dto.VideoInfoDTO
import com.movies.whattowatch.extension.getJustYear
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.uielements.BackgroundImage
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar
import com.movies.whattowatch.uielements.VideoPlayer
import eu.wewox.textflow.TextFlow
import eu.wewox.textflow.TextFlowObstacleAlignment

@Composable
fun DetailsScreen(navigate: (String) -> Unit, detailsViewModel: DetailsViewModel) {
    val viewState: DetailsViewState by detailsViewModel.viewState.collectAsState()
    DetailsScreen(
        viewState.isLoading,
        viewState.info,
        viewState.cast,
        viewState.videos,
        navigate
    )
}

@Composable
fun DetailsScreen(
    isLoading: Boolean,
    info: MovieInfo,
    cast: List<CastDTO>,
    video: List<VideoInfoDTO>,
    navigate: (String) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    TopBar(
        false,
        {},
        info.posterPath,
        navigate,
        NavigationItem.None
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            Box {
                BackgroundImage(info.posterPath, 0.5f)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
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
                                    CastInfo(cast, navigate)
                                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                        if (video.isNotEmpty()) {
                                            video.filter { info -> info.site == "YouTube" }
                                                .forEach {
                                                    VideoPlayer(it.key)
                                                }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun Providers(movieInfo: MovieInfo) {
    val uriHandler = LocalUriHandler.current
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(5.dp)) {
        if (movieInfo.providerName != null) {
            movieInfo.providerName.forEach {
                AsyncImage(
                    model = stringResource(R.string.image_path_or, it),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Provider",
                    modifier = Modifier.clickable {
                        uriHandler.openUri(movieInfo.link) }
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
    navigate: (String) -> Unit
) {
    val width = LocalConfiguration.current.screenWidthDp.dp

    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        cast.forEach {
            item {
                Card {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = stringResource(R.string.image_path, it.profile_path),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = it.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(width / 2.5f)
                                .clickable(onClick = {
                                    navigate(Screen.PERSON.name + "/${it.id}")
                                }),

                            )
                        Text(text = it.name, fontWeight = FontWeight.Bold)
                        Text(text = it.character)
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieOverview(
    info: MovieInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                val rating = info.voteAverage.toFloat() / 10
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = rating,
                        color = Color(
                            red = 1 - (rating * rating),
                            green = rating * rating,
                            blue = 0f
                        )
                    )
                    Text(text = "${(info.voteAverage * 10).toInt()}%", fontWeight = FontWeight.Bold)
                }
                Text(text = "${info.voteCount} votes")
            }
        }
    }

    Card {
        TextFlow(
            text = info.overview,
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