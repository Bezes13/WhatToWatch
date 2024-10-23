package com.movies.whattowatch.uielements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.alphaContainer
import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.enums.SortType

@Composable
fun MovieFilter(
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit,
    genres: List<Genre>,
    selectedGenre: List<Genre>
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = alphaContainer
                )
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SortType.entries.forEach {
            SortingChip(
                isSelected = sortType == it,
                onClick = { eventListener(MainViewEvent.ChangeSorting(it)) },
                text = stringResource(
                    id = it.textID
                )
            )
        }


    }

    GenreDropdown(genres, selectedGenre, eventListener)
}