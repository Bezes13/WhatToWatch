package com.movies.whattowatch.uielements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.alphaContainer
import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.enums.SortType

@Composable
fun MovieFilter(
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit,
    genres: List<Genre>
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
        SortingChip(sortType, SortType.POPULARITY, eventListener)
        SortingChip(sortType, SortType.VOTE_AVERAGE, eventListener)
        SortingChip(sortType, SortType.VOTE_COUNT, eventListener)
        SortingChip(sortType, SortType.REVENUE, eventListener)
    }

    GenreDropdown(genres, eventListener)
}