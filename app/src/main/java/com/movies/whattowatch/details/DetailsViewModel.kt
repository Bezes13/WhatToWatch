package com.movies.whattowatch.details

import androidx.lifecycle.ViewModel
import com.movies.whattowatch.MainViewEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailsViewModel : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(DetailsViewState())
    val viewState = _viewState.asStateFlow()
}
