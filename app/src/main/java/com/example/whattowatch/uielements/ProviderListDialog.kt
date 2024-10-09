package com.example.whattowatch.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.whattowatch.MainViewEvent
import com.example.whattowatch.R
import com.example.whattowatch.dataClasses.Provider

@Composable
fun ProviderListDialog(providers: List<Provider>, eventListener: (MainViewEvent) -> Unit, onDismissRequest: () -> Unit) {
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
                Row {
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 30.sp, textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Streaming Dienste")
                        }
                    })
                }
                Divider()
                Text(text = stringResource(R.string.provider_text))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    modifier = Modifier.fillMaxHeight(0.8f)
                ) {
                    items(providers) {
                        AsyncImage(
                            model = stringResource(R.string.image_path, it.logoPath),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = it.providerName,
                            modifier = Modifier
                                .size(128.dp)
                                .clickable(onClick = {
                                    eventListener(
                                        MainViewEvent.UpdateProvider(
                                            it.providerId,
                                            !it.show
                                        )
                                    )
                                })
                        )
                        if(it.show){
                            Image(
                                painter = painterResource(id = R.drawable.checked),
                                contentDescription = stringResource(id = R.string.checked),
                                alpha = 0.5f,
                                modifier = Modifier.size(128.dp)
                            )
                        }

                    }
                }

                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(5.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Close")
                }
            }
        }
    }
}