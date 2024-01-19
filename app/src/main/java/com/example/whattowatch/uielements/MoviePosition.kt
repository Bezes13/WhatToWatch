package com.example.whattowatch.uielements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.R
import com.example.whattowatch.extension.getJustYear

@Composable
fun MoviePosition(movieInfo: MovieInfo, selectedGenre: String, saveSeen: (String, Int, Int) -> Unit) {
    Row {
        Column(modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = movieInfo.user, textAlign = TextAlign.Center)
            Text(text = movieInfo.title, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text(text = movieInfo.release_date.getJustYear(), textAlign = TextAlign.Center)
            Row {
                if(movieInfo.provider_name != null){
                    movieInfo.provider_name.forEach{
                        AsyncImage(
                            model = stringResource(R.string.image_path_or, it),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Provider",
                        )
                    }
                    if(movieInfo.provider_name.isEmpty()){
                        Image(
                            modifier = Modifier.size(50.dp),
                            painter = painterResource(id = R.drawable.na),
                            contentDescription = stringResource(id = R.string.not_available)
                        )
                    }
                }
            }
        }
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.End){
            Button(onClick = { saveSeen(selectedGenre,movieInfo.id, R.string.seen) }) {
                Text("Gesehen")
            }
            Button(onClick = { saveSeen(selectedGenre,movieInfo.id, R.string.later) }) {
                Text("Später")
            }
            Button(onClick = { saveSeen(selectedGenre,movieInfo.id, R.string.no) }) {
                Text("Nein")
            }
        }
        AsyncImage(
            modifier = Modifier.clickable(onClick = {}),
            model = stringResource(R.string.image_path, movieInfo.poster_path),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = movieInfo.title,
        )
    }
}

@Preview
@Composable
fun PreviewPosition(){
    MoviePosition(movieInfo = MovieInfo(1,"","",3,",",",",",",3,3), selectedGenre = "", saveSeen = { _, _, _->})
}