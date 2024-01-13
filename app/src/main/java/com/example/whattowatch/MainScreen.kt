package com.example.whattowatch

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo


@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()

    //if (viewState.genres.isNotEmpty() && viewState.movies.isEmpty())
      //  mainViewModel.getMovies(viewState.genres[0])

    MainScreenContent(viewState.movies, viewState.genres,mainViewModel.markFilmAs, mainViewModel::getMovies,mainViewModel::getCustomList, mainViewModel::getProvider, mainViewModel::saveSharedList)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    movies: Map<String, List<MovieInfo>>,
    genres: List<Genre>,
    additionalGenres:List<String>,
    getMovies: (Genre) -> Unit,
    getCustomList: (String) -> Unit,
    getProvider: (String, Int) -> Unit,
    saveSeen: (String, Int, Int) -> Unit
) {
    if (genres.isNotEmpty()) {

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("What to watch")
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(innerPadding)
            ) {

                val selectedGenre = genreDropdown(genres, getMovies, additionalGenres, getCustomList)

                LazyColumn {
                    item {
                        movies[selectedGenre]?.forEach {
                            //getProvider(selectedGenre, it.id)
                            Row {
                                Column(modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = it.title, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                    Text(text = it.release_date.getJustYear(), textAlign = TextAlign.Center)
                                    Row {
                                        if(it.provider_name != null){
                                            it.provider_name.forEach{
                                                AsyncImage(
                                                    model = stringResource(R.string.image_path_or, it),
                                                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                                                    contentDescription = "Provider",
                                                )
                                            }
                                            if(it.provider_name.isEmpty()){
                                                Image(
                                                    modifier = Modifier.size(50.dp),
                                                    painter = painterResource(id = R.drawable.na),
                                                    contentDescription = stringResource(id = R.string.not_available)
                                                )
                                            }
                                        }
                                    }
                                }
                                AsyncImage(
                                    modifier = Modifier.clickable(onClick = {saveSeen(selectedGenre,it.id, R.string.seen)}),
                                    model = stringResource(R.string.image_path, it.poster_path),
                                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = it.title,
                                )
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

private fun String.getJustYear(): String {
    return this.substring(0,4)
}

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreen()
}