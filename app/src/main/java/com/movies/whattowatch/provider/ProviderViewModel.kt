package com.movies.whattowatch.provider

import androidx.lifecycle.ViewModel
import com.movies.whattowatch.MainViewEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProviderViewModel : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(ProviderViewState())
    val viewState = _viewState.asStateFlow()
}
