package com.example.whattowatch.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.whattowatch.R
import com.example.whattowatch.datas.MovieInfo
import com.example.whattowatch.dto.CastDTO

@Composable
fun PersonDetailsDialog(info: CastDTO, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = info.name, modifier = Modifier.weight(0.7f))
                    AsyncImage(
                        model = stringResource(R.string.image_path, info.profile_path),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = info.name,
                        modifier = Modifier.weight(0.3f)
                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .wrapContentHeight()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 5.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        info.credits.forEach {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = it.title,modifier = Modifier.weight(0.7f))
                                    AsyncImage(
                                        model = stringResource(
                                            R.string.image_path,
                                            it.posterPath
                                        ),
                                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = it.title,
                                        modifier = Modifier.size(80.dp).weight(0.3f)
                                    )
                                }

                            }

                        }
                    }
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPersonDetails(){
    PersonDetailsDialog(info = CastDTO("Tom Johnson", "", 3, listOf(
        MovieInfo(
        231,
        "Englsich",
        "Puss in Boots discovers that his passion for adventure has taken its toll: He has burned through eight of his nine lives, leaving him with only one life left. Puss sets out on an epic journey to find the mythical Last Wish and restore his nine lives.",
        12,
        "pasdl",
        "2022",
        "Puss in Boots: The Last Wish",
        8.3,
        6891,
        listOf("Netflix"),
        cast = listOf()
    ),MovieInfo(
            231,
            "Englsich",
            "Puss in Boots discovers that his passion for adventure has taken its toll: He has burned through eight of his nine lives, leaving him with only one life left. Puss sets out on an epic journey to find the mythical Last Wish and restore his nine lives.",
            12,
            "pasdl",
            "2022",
            "Puss in Boots: The Last Wish",
            8.3,
            6891,
            listOf("Netflix"),
            cast = listOf()
        ),MovieInfo(
            231,
            "Englsich",
            "Puss in Boots discovers that his passion for adventure has taken its toll: He has burned through eight of his nine lives, leaving him with only one life left. Puss sets out on an epic journey to find the mythical Last Wish and restore his nine lives.",
            12,
            "pasdl",
            "2022",
            "Puss in Boots: The Last Wish",
            8.3,
            6891,
            listOf("Netflix"),
            cast = listOf()
        )
    ))) {}
}