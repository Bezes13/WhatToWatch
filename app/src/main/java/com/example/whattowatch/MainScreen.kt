package com.example.whattowatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whattowatch.TestData.movie1
import com.example.whattowatch.TestData.movie2
import com.example.whattowatch.TestData.testGenre
import com.example.whattowatch.dataClasses.Genre
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dataClasses.Provider
import com.example.whattowatch.dto.CastDTO
import com.example.whattowatch.dto.VideoInfoDTO
import com.example.whattowatch.enums.SortType
import com.example.whattowatch.enums.UserMark
import com.example.whattowatch.uielements.GenreDropdown
import com.example.whattowatch.uielements.MovieDetailsDialog
import com.example.whattowatch.uielements.MovieListOverview
import com.example.whattowatch.uielements.PersonDetailsDialog
import com.example.whattowatch.uielements.ProviderListDialog
import com.example.whattowatch.uielements.SortingChip
import com.example.whattowatch.uielements.TopBar

@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    val viewState: MainViewState by mainViewModel.viewState.collectAsState()
    MainScreenContent(
        viewState.isLoading,
        viewState.selectedGenre,
        viewState.shows,
        if(viewState.showMovies) viewState.genres else viewState.seriesGenres,
        viewState.providers,
        viewState.sorting,
        mainViewModel::getMovies,
        mainViewModel::getCustomList,
        viewState.dialog,
        mainViewModel::changeLoadedMovies,
        mainViewModel::sendEvent,
        mainViewModel::getCast,
        mainViewModel::getCredits,
        viewState.loadMore
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    selectedGenre: String,
    movies: Map<String, List<MovieInfo>>,
    genres: List<Genre>,
    allProviders: List<Provider>,
    sortType: SortType,
    getMovies: (Genre) -> Unit,
    getCustomList: (UserMark) -> Unit,
    dialog: MainViewDialog,
    changeLoadedMovies: (String) -> Unit,
    eventListener: (MainViewEvent) -> Unit,
    getCast: (MovieInfo) -> Unit,
    getCredits: (CastDTO) -> Unit,
    loadMore: Boolean
) {
    if (genres.isNotEmpty()) {
        var showFilter by remember { mutableStateOf(true) }
        TopBar(eventListener, showFilter, { showFilter = !showFilter } ) { innerPadding ->
            when (dialog) {
                is MainViewDialog.DetailsDialog -> MovieDetailsDialog(
                    dialog.info,
                    dialog.cast,
                    dialog.video,
                    getCredits = getCredits,
                    onDismissRequest = {
                        eventListener(
                            MainViewEvent.SetDialog(MainViewDialog.None)
                        )
                    }
                )

                is MainViewDialog.PersonDetails -> PersonDetailsDialog(dialog.info, getCast) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                is MainViewDialog.ShowProviderList -> ProviderListDialog(allProviders, eventListener) {
                    eventListener(
                        MainViewEvent.SetDialog(MainViewDialog.None)
                    )
                }

                else -> {}
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(!showFilter){
                    Row (modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxWidth()){
                        SortingChip(sortType, SortType.POPULARITY, eventListener)
                        SortingChip(sortType, SortType.VOTE_AVERAGE, eventListener)
                        SortingChip(sortType, SortType.VOTE_COUNT, eventListener)
                        SortingChip(sortType, SortType.REVENUE, eventListener)
                    }

                    GenreDropdown(genres, getMovies, getCustomList, eventListener)
                }

                if (selectedGenre == "") {
                    Spacer(modifier = Modifier.size(100.dp))
                    Text(
                        text = stringResource(R.string.choose_genre),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                if (movies[selectedGenre] != null && (movies[selectedGenre]
                        ?: listOf()).isNotEmpty()
                ) {
                    MovieListOverview(
                        movies = movies,
                        selectedGenre = selectedGenre,
                        eventListener = eventListener,
                        getCast = getCast,
                        isLoading = isLoading,
                        changeLoadedMovies = changeLoadedMovies,
                        loadMore = loadMore
                    )
                } else {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterHorizontally)
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
                }
            }
        }
    }
}

sealed class MainViewDialog {
    data class DetailsDialog(val info: MovieInfo, val cast: List<CastDTO>, val video: List<VideoInfoDTO>) : MainViewDialog()
    data object None : MainViewDialog()
    data class PersonDetails(val info: CastDTO) : MainViewDialog()
    data object ShowProviderList : MainViewDialog()
}

@Composable
@Preview
fun PreviewMainScreen() {
    MainScreenContent(
        isLoading = false,
        selectedGenre = testGenre,
        movies = mapOf(
            Pair(
                testGenre, listOf(
                    movie1,
                    movie1,
                    movie2,
                )
            )
        ),
        genres = listOf(Genre(3, testGenre)),
        allProviders = listOf(),
        sortType = SortType.POPULARITY,
        getMovies = {},
        getCustomList = {},
        dialog = MainViewDialog.None,
        changeLoadedMovies = {},
        eventListener = {},
        getCast = { },
        getCredits = {},
        true
    )
}
