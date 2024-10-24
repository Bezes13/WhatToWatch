package com.movies.whattowatch.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector
import com.movies.whattowatch.R

enum class UserMark(val textID: Int, val icon: ImageVector) {

    SEEN(R.string.seen, icon = Icons.Default.Favorite),
    LATER(R.string.later, icon = Icons.Default.Add),
    NO(R.string.no, icon = Icons.Default.Delete);
}