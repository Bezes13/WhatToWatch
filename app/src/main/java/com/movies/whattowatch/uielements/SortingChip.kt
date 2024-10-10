package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.enums.SortType
import com.movies.whattowatch.enums.UserMark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingChip(
    currentSortType: SortType,
    sortType: SortType,
    eventListener: (MainViewEvent) -> Unit
) {
    val selected = currentSortType == sortType
    FilterChip(
        onClick = { eventListener(MainViewEvent.ChangeSorting(sortType)) },
        label = {
            Text(stringResource(id = sortType.textID))
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.MarkingChips(
    userMark: UserMark,
    selectedGenre: String,
    orientation: Orientation,
    eventListener: (MainViewEvent) -> Unit
) {
    val selected = selectedGenre == userMark.name
    FilterChip(
        modifier = Modifier.weight(1f),
        onClick = { eventListener(MainViewEvent.ShowCustom(userMark)) },
        label = {
            Text(
                stringResource(id = userMark.textID),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        selected = selected,
        shape = when (orientation) {
            Orientation.Left -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
            Orientation.Center -> RectangleShape
            Orientation.Right -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

enum class Orientation {
    Left, Center, Right
}