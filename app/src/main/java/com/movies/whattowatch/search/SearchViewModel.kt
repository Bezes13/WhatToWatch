package com.movies.whattowatch.search

import androidx.lifecycle.ViewModel
import com.movies.whattowatch.MainViewEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(SearchViewState())
    val viewState = _viewState.asStateFlow()
}
