package com.movies.whattowatch.person


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.TestData
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dto.PersonDTO
import com.movies.whattowatch.extension.getJustYear
import com.movies.whattowatch.formatDateString
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar
import com.movies.whattowatch.yearsSince

@Composable
fun PersonScreen(navigate: (String) -> Unit, viewModel: PersonViewModel) {
    val viewState: PersonViewState by viewModel.viewState.collectAsState()
    PersonScreen(
        viewState.isLoadingCredits,
        viewState.isLoadingDetails,
        viewState.movies,
        viewState.personDTO,
        navigate
    )
}

@Composable
fun PersonScreen(
    isLoadingCredits: Boolean,
    isLoadingDetails: Boolean,
    movies: List<MovieInfo>,
    personDTO: PersonDTO,
    navigate: (String) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    TopBar(
        false,
        {},
        if (movies.isEmpty()) "" else movies[0].posterPath,
        navigate,
        NavigationItem.None
    ) { innerPadding ->
        Box {
            if (movies.isNotEmpty()) {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(),
                    model = stringResource(R.string.image_path, movies[0].posterPath),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = stringResource(R.string.background),
                    alpha = 0.5f
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(innerPadding),
            ) {

                if (isExpanded) {
                    AsyncImage(
                        model = stringResource(R.string.image_path, personDTO.profile_path),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = personDTO.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = { isExpanded = false })
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (isLoadingDetails) {
                            LoadingField()
                        } else {
                            TopView(personDTO) { isExpanded != isExpanded }
                        }
                        Divider()
                        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
                            movies.forEach { movieInfo ->
                                item {
                                    Card(modifier = Modifier.padding(5.dp)) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(bottom = 5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .clickable(onClick = {
                                                        navigate(Screen.DETAILS.name + "/${movieInfo.id}/${movieInfo.isMovie}")
                                                    })
                                                    .fillMaxWidth(1f),
                                                model = stringResource(
                                                    R.string.image_path,
                                                    movieInfo.posterPath
                                                ),
                                                contentScale = ContentScale.Crop,
                                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                                error = painterResource(id = R.drawable.ic_launcher_foreground),
                                                contentDescription = movieInfo.title,
                                            )

                                            Text(
                                                text = movieInfo.releaseDate.getJustYear(),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )

                                            if (movieInfo.providerName != null) {
                                                LazyRow(
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        5.dp
                                                    )
                                                ) {
                                                    movieInfo.providerName.forEach {
                                                        item {
                                                            AsyncImage(
                                                                model = stringResource(
                                                                    R.string.image_path_or,
                                                                    it
                                                                ),
                                                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                                                error = painterResource(id = R.drawable.ic_launcher_foreground),
                                                                contentDescription = "Provider",
                                                            )
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
    }
}


@Composable
private fun LoadingField() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(150, 150, 150, 120))
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}


@Composable
private fun TopView(personDTO: PersonDTO, expand: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        AsyncImage(
            model = stringResource(R.string.image_path, personDTO.profile_path),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = personDTO.name,
            modifier = Modifier
                .weight(0.5f)
                .clickable(onClick = { expand() })
                .clip(RoundedCornerShape(10))
        )

        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = personDTO.name)
            Text(text = "Age: ${yearsSince(personDTO.birthday)} (${formatDateString(personDTO.birthday)})")
            if (personDTO.deathday != null)
                Text(text = "Deathday: (${formatDateString(personDTO.deathday)})")
        }
    }
}

@Preview
@Composable
fun PersonPreview() {
    PersonScreen(
        isLoadingCredits = false,
        isLoadingDetails = false,
        movies = TestData.testMovies,
        personDTO = TestData.testPerson,
        navigate = {}
    )
}
