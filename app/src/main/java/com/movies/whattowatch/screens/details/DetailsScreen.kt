package com.movies.whattowatch.screens.details

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.movies.whattowatch.ProviderShape
import com.movies.whattowatch.R
import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dto.CastDTO
import com.movies.whattowatch.model.dto.VideoInfoDTO
import com.movies.whattowatch.extension.getJustYear
import com.movies.whattowatch.getRevenue
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.uielements.BackgroundImage
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar
import com.movies.whattowatch.uielements.VideoPlayer

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
        null,
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
                        LazyColumn(
                            Modifier
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                MyCard {
                                    Header(info)
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            PosterCard(info) { isExpanded = true }
                                            Column {
                                                if(info.isMovie){
                                                    Text(text = "${info.runtime} min.")
                                                    Text(text = info.revenue.getRevenue())
                                                } else {
                                                    val season = if (info.numberSeasons == 1) "Season" else "Seasons"
                                                    val episodes = if (info.numberEpisodes == 1) "Episode" else "Episodes"
                                                    Text(text = "${info.numberSeasons} $season")
                                                    Text(text = "${info.numberEpisodes} $episodes")
                                                }
                                                Providers(info)
                                            }
                                        }
                                        VoteCard(info)
                                    }
                                    OverviewCard(info)
                                }
                            }

                            item { CastInfo(cast, navigate) }
                            item {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    if (video.isNotEmpty()) {
                                        video.filter { info -> info.site == "YouTube" }
                                            .forEach {
                                                item {
                                                    MyCard {
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
}


@Composable
fun Providers(movieInfo: MovieInfo) {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        movieInfo.providerName?.chunked(3)?.forEach {
            Row(horizontalArrangement = Arrangement.Start) {
                it.forEach {
                    AsyncImage(
                        model = stringResource(R.string.image_path_or, it),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Provider",
                        modifier = Modifier.clip(ProviderShape)
                    )
                }
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

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        cast.forEach {
            item {
                MyCard {
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
                        Text(text = it.name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp))
                        Text(text = it.character, modifier = Modifier.padding(bottom = 5.dp, start = 5.dp, end = 5.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(info: MovieInfo) {
    Text(info.overview, Modifier.padding(5.dp))
}

@Composable
private fun PosterCard(
    info: MovieInfo,
    onClick: () -> Unit
) {
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
            .clip(RoundedCornerShape(10))
    )

}

@Composable
private fun VoteCard(info: MovieInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(5.dp)
    ) {
        val rating = info.voteAverage.toFloat() / 10
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = rating,
                color = Color(
                    red = 1 - (rating * rating),
                    green = rating * rating,
                    blue = 0f
                ),
                modifier = Modifier.size(128.dp),
                strokeWidth = 10.dp
            )
            Text(
                text = "${(info.voteAverage * 10).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp
            )
        }
        //Text(text = "${info.voteCount} votes")
    }
}

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
    alpha: Float = 0.7f,
    shape: Shape = CardDefaults.shape,
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(),
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha
            )
        )
    ) {
        content()
    }
}


@Composable
private fun Header(info: MovieInfo) {

    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
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
    }, modifier = Modifier.padding(5.dp))

}