package com.movies.whattowatch.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.model.enums.SortType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    private val _event = MutableSharedFlow<String>()
    private val _viewState = MutableStateFlow(SearchViewState())
    val viewState = _viewState.asStateFlow()

    init {
        listenToEvent()
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch{
            val foundObjects = apiRepository.getMovies(1, listOf(), listOf(),true, SortType.POPULARITY)
            _viewState.update { currentState ->
                currentState.copy(founds = foundObjects, isLoading = false)
            }
        }

    }
    fun sendEvent(event: String) {
        viewModelScope.launch(ioDispatcher) {
            _event.emit(event)
        }
    }

    private fun listenToEvent() = viewModelScope.launch(ioDispatcher) {
        _event.collect { event ->
            fetchSearchEntries(event)
        }
    }

    private fun fetchSearchEntries(text: String) {
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val foundObjects = apiRepository.getSearch(text, 1)
            _viewState.update { currentState ->
                currentState.copy(founds = foundObjects, isLoading = false)
            }
        }
    }
}
