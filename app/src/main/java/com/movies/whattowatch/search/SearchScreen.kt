package com.movies.whattowatch.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.alphaContainer
import com.movies.whattowatch.dataClasses.MediaType
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.details.MyCard
import com.movies.whattowatch.extension.getJustYear
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.uielements.BackgroundImage
import com.movies.whattowatch.uielements.LoadingBox
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar

@Composable
fun SearchScreen(navigate: (String) -> Unit, searchViewModel: SearchViewModel) {
    val viewState: SearchViewState by searchViewModel.viewState.collectAsState()
    SearchScreen(
        viewState.isLoading,
        viewState.founds,
        searchViewModel::sendEvent,
        navigate
    )
}

@Composable
fun SearchScreen(
    isLoading: Boolean,
    founds: List<MovieInfo>,
    eventListener: (String) -> Unit,
    navigate: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    TopBar(
        null,
        {},
        if (founds.isEmpty()) "" else founds[0].posterPath,
        navigate,
        NavigationItem.SEARCH
    ) { innerPadding ->
        Box {
            if (founds.isNotEmpty()) {
                BackgroundImage(image = founds[0].posterPath, alpha = alphaContainer)
            } else {
                Image(painterResource(id = R.drawable.wtw), "", Modifier.fillMaxSize())
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    textStyle = TextStyle(fontWeight = FontWeight.Bold),
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        eventListener(searchText)
                    },
                    placeholder = { Text("Search For ...", fontWeight = FontWeight.Bold) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            0.7f
                        ),
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            0.7f
                        ),
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            0.7f
                        )
                    )
                )
                if (!isLoading) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalItemSpacing = 5.dp
                    ) {
                        founds.forEach {
                            item {
                                MyCard {
                                    AsyncImage(
                                        model = stringResource(R.string.image_path, it.posterPath),
                                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = it.title,
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable(
                                                onClick = {
                                                    if (it.mediaType == MediaType.PERSON)
                                                        navigate(Screen.PERSON.name + "/${it.id}")
                                                    else
                                                        navigate(Screen.DETAILS.name + "/${it.id}/${it.isMovie}")
                                                }
                                            )
                                    )
                                    Text(
                                        text = it.title,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = it.releaseDate.getJustYear(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 5.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LoadingBox()
                }
            }
        }
    }

}