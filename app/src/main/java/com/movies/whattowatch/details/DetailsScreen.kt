package com.movies.whattowatch.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun DetailsScreen(navigate: (String) -> Unit, detailsViewModel: DetailsViewModel) {
    val viewState: DetailsViewState by detailsViewModel.viewState.collectAsState()
    DetailsScreen(
        viewState.loading
    )
}

@Composable
fun DetailsScreen(isLoading: Boolean) {

}