package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.enums.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingChip(currentSortType: SortType, sortType: SortType, eventListener: (MainViewEvent) -> Unit) {
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