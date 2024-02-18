package com.example.whattowatch.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.dataClasses.MediaType
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dto.CastDTO

@Composable
fun Search(
    founds: List<MovieInfo>,
    loadMore: Boolean,
    page: Int,
    eventListener: (MainViewEvent) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        var searchText by remember { mutableStateOf("") }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black)
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
                        eventListener(MainViewEvent.SearchFor(searchText, 1, founds))
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
                                    .clickable(onClick = {
                                        eventListener(
                                            if (it.mediaType == MediaType.PERSON) MainViewEvent.FetchCredits(
                                                CastDTO(
                                                    it.title,
                                                    it.posterPath,
                                                    it.id,
                                                    it.knownFor ?: listOf()
                                                )
                                            ) else
                                                MainViewEvent.FetchCast(it)
                                        )
                                    })
                            )
                            Text(text = it.title)
                        }
                    }
                    item {
                        if (loadMore)
                            Button(
                                onClick = {
                                    eventListener(
                                        MainViewEvent.SearchFor(
                                            searchText,
                                            page + 1,
                                            founds
                                        )
                                    )
                                },
                            ) {
                                Text(text = stringResource(id = R.string.load_more_movies))
                            }
                    }
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(5.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
@Preview
fun SearchPreview() {
    Search(founds = listOf(), true, 2, eventListener = {}) {

    }
}



