package com.movies.whattowatch.uielements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.movies.whattowatch.screens.main.MainViewEvent
import com.movies.whattowatch.alphaContainer
import com.movies.whattowatch.model.enums.UserMark

@Composable
fun MarkedFilmsHeader(
    selectedGenre: String,
    eventListener: (MainViewEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = alphaContainer))
            .fillMaxWidth()
    ) {
        UserMark.entries.forEach {
            MarkingChips(
                userMark = it,
                selectedGenre = selectedGenre,
                orientation = when (it) {
                    UserMark.entries.first() -> Orientation.Left
                    UserMark.entries.last() -> Orientation.Right
                    else -> Orientation.Center
                },
                eventListener = eventListener
            )

        }
    }
}