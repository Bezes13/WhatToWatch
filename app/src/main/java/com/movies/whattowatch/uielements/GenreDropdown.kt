package com.movies.whattowatch.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.movies.whattowatch.screens.main.MainViewEvent
import com.movies.whattowatch.R
import com.movies.whattowatch.model.dataClasses.Genre
import com.movies.whattowatch.screens.details.MyCard


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreDropdown(
    items: List<Genre>,
    selectedGenres: List<Genre>,
    eventListener: (MainViewEvent) -> Unit
) {

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth().padding(10.dp)
    ) {
        items.forEach {
            val selected = selectedGenres.contains(it)
            if (selected) {
                MyCard(
                    modifier = Modifier.clickable { eventListener(MainViewEvent.SetGenre(it)) },
                    alpha = 1f
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check, contentDescription = stringResource(
                                R.string.selected_label
                            )
                        )
                        Text(text = it.name, modifier = Modifier.padding(5.dp))
                    }

                }
            } else {
                MyCard(
                    modifier = Modifier.clickable { eventListener(MainViewEvent.SetGenre(it)) },
                    border = BorderStroke(
                        0.dp,
                        Color.Black
                    )
                ) {
                    Text(text = it.name, modifier = Modifier.padding(5.dp))
                }
            }


        }
    }


}

@Composable
@Preview
fun GenrePreview() {
    GenreDropdown(
        items = listOf(
            Genre(1, "Peter"),
            Genre(2, "Fridoline"),
            Genre(4, "Abenteuer"),
            Genre(3, "Scy-Fy"),
            Genre(5, "Romatnitk"),
            Genre(6, "Max Kruse")
        ), selectedGenres = listOf(), eventListener = {})
}
