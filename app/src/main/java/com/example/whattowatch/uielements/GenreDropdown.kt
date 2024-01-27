package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.dataClasses.Genre
import com.example.whattowatch.enums.UserMark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdown(
    items: List<Genre>,
    getMovies: (Genre) -> Unit,
    getCustomList: (UserMark) -> Unit,
    eventListener: (MainViewEvent) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var genre by remember { mutableStateOf("No Genre") }

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
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            placeholder = { Text(text = "Select Genre") },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
    }
    DropdownMenu(
        expanded = isExpanded, onDismissRequest = {
            isExpanded = false
        }, modifier = Modifier.fillMaxWidth()
    ) {
        DropdownMenuItem(text = {
            Text(text = "No Genre", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }, onClick = {
            genre = "No Genre"
            isExpanded = false
            getMovies(Genre(-1,"No Genre"))
            eventListener(MainViewEvent.SetGenre(genre))
        }, modifier = Modifier.fillMaxWidth()
        )
        Divider()
        items.forEach {
            DropdownMenuItem(text = {
                Text(text = it.name, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }, onClick = {
                genre = it.name
                isExpanded = false
                getMovies(items.first { it.name == genre })
                eventListener(MainViewEvent.SetGenre(genre))
            }, modifier = Modifier.fillMaxWidth()
            )
            Divider()
        }
        UserMark.entries.forEach {
            val text = stringResource(id = it.textID)
            DropdownMenuItem(
                text = {
                    Text(text = text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth() )
                },
                onClick = {
                    genre = text
                    isExpanded = false
                    getCustomList(it)
                    eventListener(MainViewEvent.SetGenre(it.name))
                },
                modifier = Modifier.fillMaxWidth()
            )
            Divider()
        }
    }
}
