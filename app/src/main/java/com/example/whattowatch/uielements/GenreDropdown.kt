package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.whattowatch.dto.SingleGenreDTO
import com.example.whattowatch.MainViewEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun genreDropdown(
    items: List<SingleGenreDTO>,
    getMovies: (SingleGenreDTO) -> Unit,
    additionalItems: List<String>,
    getCustomList: (String) -> Unit,
    eventListener: (MainViewEvent) -> Unit
): String {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var genre by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = genre,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            placeholder = {
                Text(text = "Select Genre")
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
    }
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            isExpanded = false
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach {
            DropdownMenuItem(
                text = {
                    Text(text = it.name)
                },
                onClick = {
                    genre = it.name
                    isExpanded = false
                    getMovies(items.first { it.name == genre })
                    eventListener(MainViewEvent.SetGenre(genre))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        additionalItems.forEach {
            DropdownMenuItem(
                text = {
                    Text(text = it)
                },
                onClick = {
                    genre = it
                    isExpanded = false
                    getCustomList(it)
                    eventListener(MainViewEvent.SetGenre(genre))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

    return genre
}
