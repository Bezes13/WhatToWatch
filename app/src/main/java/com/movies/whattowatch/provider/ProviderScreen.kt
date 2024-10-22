package com.movies.whattowatch.provider

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.movies.whattowatch.ProviderShape
import com.movies.whattowatch.R
import com.movies.whattowatch.dataClasses.Provider
import com.movies.whattowatch.details.MyCard
import com.movies.whattowatch.uielements.LoadingBox
import com.movies.whattowatch.uielements.NavigationItem
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
    val width = LocalConfiguration.current.screenWidthDp.dp

    val providerSize = width.div(3.5f)
    TopBar(
        false,
        {},
        if (providers.isEmpty()) "" else providers[0].logoPath,
        navigate,
        NavigationItem.SELECTION
    ) { innerPadding ->
       MyCard {
            if (!isLoading) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 128.dp),
                    ) {
                        items(providers) {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = stringResource(R.string.image_path, it.logoPath),
                                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = it.providerName,
                                    modifier = Modifier
                                        .size(providerSize)
                                        .clip(ProviderShape)
                                        .clickable(onClick = {
                                            eventListener(it)
                                        })
                                )
                                if (it.show) {
                                    Image(
                                        painter = painterResource(id = R.drawable.checked),
                                        contentDescription = stringResource(id = R.string.checked),
                                        alpha = 1f,
                                        modifier = Modifier.size(providerSize / 2)
                                    )
                                }
                                if (it.isUpdating) {
                                    LoadingBox(providerSize)
                                }
                            }
                        }
                    }
                }
            } else {
                LoadingBox()
            }

    }}
}
