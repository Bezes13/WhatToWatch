package com.movies.whattowatch.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun ProviderScreen(navigate: (String) -> Unit, providerViewModel: ProviderViewModel) {
    val viewState: ProviderViewState by providerViewModel.viewState.collectAsState()
    ProviderScreen(
        viewState.isLoading
    )
}

@Composable
fun ProviderScreen(isLoading: Boolean) {

}
