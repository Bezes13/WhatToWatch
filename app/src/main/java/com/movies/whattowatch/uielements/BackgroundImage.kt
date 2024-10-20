package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.movies.whattowatch.R

@Composable
fun BackgroundImage(image: String) {
    AsyncImage(
        modifier = Modifier.fillMaxHeight(),
        model = stringResource(R.string.image_path, image),
        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
        error = painterResource(id = R.drawable.ic_launcher_foreground),
        contentScale = ContentScale.FillBounds,
        contentDescription = stringResource(R.string.background),
    )
}