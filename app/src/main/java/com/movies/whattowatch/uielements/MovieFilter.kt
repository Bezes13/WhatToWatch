package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.movies.whattowatch.screens.main.MainViewEvent
import com.movies.whattowatch.model.dataClasses.Genre
import com.movies.whattowatch.screens.details.MyCard
import com.movies.whattowatch.model.enums.SortType

@Composable
fun MovieFilter(
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit,
    genres: List<Genre>,
    selectedGenre: List<Genre>
) {
    MyCard(
        shape = RoundedCornerShape(0,0,10,10),
        modifier = Modifier.padding(horizontal = 5.dp)
    ) {
        Row(
            modifier = Modifier
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



}