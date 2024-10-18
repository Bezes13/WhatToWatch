package com.movies.whattowatch.provider

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.movies.whattowatch.R
import com.movies.whattowatch.dataClasses.Provider
import com.movies.whattowatch.uielements.TopBar


@Composable
fun ProviderScreen(navigate: (String) -> Unit, providerViewModel: ProviderViewModel) {
    val viewState: ProviderViewState by providerViewModel.viewState.collectAsState()
    ProviderScreen(
        viewState.isLoading,
        viewState.providers,
        providerViewModel::sendEvent,
        navigate
    )
}

@Composable
fun ProviderScreen(
    isLoading: Boolean,
    providers: List<Provider>,
    eventListener: (Provider) -> Unit,
    navigate: (String) -> Unit,
) {
    TopBar(
        false,
        {},
        if (providers.isEmpty()) "" else providers[0].logoPath,
        navigate
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
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
                                .size(128.dp).clip(RoundedCornerShape(10))
                                .clickable(onClick = {
                                    eventListener(it)
                                })
                        )
                        if (it.show) {
                            Image(
                                painter = painterResource(id = R.drawable.checked),
                                contentDescription = stringResource(id = R.string.checked),
                                alpha = 0.5f,
                                modifier = Modifier.size(128.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}
