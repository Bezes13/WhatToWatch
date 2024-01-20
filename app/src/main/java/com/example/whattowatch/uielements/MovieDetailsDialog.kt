package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.R
import com.example.whattowatch.extension.getJustYear

@Composable
fun MovieDetailsDialog (info:MovieInfo, onDismissRequest: () -> Unit){
    AlertDialog(
        title = {
            Row {
                Text(text = info.title)
                Text(text = info.release_date.getJustYear())
            }

        },
        text = {
            Row {
                Column (Modifier.weight(1F)){
                    Text(text = "${info.vote_average} by ${info.vote_count} - ${info.popularity}")
                    Text(text = info.overview)
                }
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1F),
                    model = stringResource(R.string.image_path, info.poster_path),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = info.title,
                )
            }

        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Close")
            }
        }
    )
}

@Preview
@Composable
fun MovieDialogPreview(){
    MovieDetailsDialog(
        MovieInfo(231, "Englsich", "Toller Film", 12, "pasdl", "22.02.2022", "Marsianer", 123,123,
            listOf("Netflix")
        ), {}
    )
}