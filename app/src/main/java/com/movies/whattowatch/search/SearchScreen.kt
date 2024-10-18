package com.movies.whattowatch.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.dataClasses.MediaType
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.navigation.Screen
import com.movies.whattowatch.uielements.NavigationItem
import com.movies.whattowatch.uielements.TopBar

@Composable
fun SearchScreen(navigate: (String) -> Unit, searchViewModel: SearchViewModel) {
    val viewState: SearchViewState by searchViewModel.viewState.collectAsState()
    SearchScreen(
        viewState.isLoading,
        viewState.founds,
        viewState.loadMore,
        viewState.page,
        searchViewModel::sendEvent,
        navigate
    )
}

@Composable
fun SearchScreen(
    isLoading: Boolean,
    founds: List<MovieInfo>,
    loadMore: Boolean,
    page: Int,
    eventListener: (String) -> Unit,
    navigate: (String) -> Unit
) {
    print(founds)
    var searchText by remember { mutableStateOf("") }
    TopBar(
        false,
        {},
        if (founds.isEmpty()) "" else founds[0].posterPath,
        navigate,
        NavigationItem.SEARCH
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        eventListener(searchText)
                        println(searchText)
                    },
                    label = { Text("Search For ...") }
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    modifier = Modifier.fillMaxHeight(0.8f)
                ) {
                    items(founds) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = stringResource(R.string.image_path, it.posterPath),
                                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                error = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = it.title,
                                modifier = Modifier
                                    .size(128.dp)
                                    .clickable(
                                        onClick = {
                                            if (it.mediaType == MediaType.PERSON)
                                                navigate(Screen.PERSON.name + "/${it.id}")
                                            else
                                                navigate(Screen.DETAILS.name + "/${it.id}/${it.isMovie}")
                                        }
                                    )
                            )
                        }
                        Text(text = it.title)
                    }
                }
            }
        }

    }
}