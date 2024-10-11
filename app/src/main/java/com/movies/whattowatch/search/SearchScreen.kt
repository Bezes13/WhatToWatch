package com.movies.whattowatch.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SearchScreen(navigate: (String) -> Unit, searchViewModel: SearchViewModel) {
    val viewState: SearchViewState by searchViewModel.viewState.collectAsState()
    SearchScreen(
        viewState.loading
    )
}

@Composable
fun SearchScreen(isLoading: Boolean) {

}