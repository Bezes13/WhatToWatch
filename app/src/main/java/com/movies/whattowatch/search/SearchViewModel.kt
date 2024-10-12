package com.movies.whattowatch.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.apiRepository.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
            val (foundObjects, _) = apiRepository.getSearch("", 1)
            _viewState.update { currentState ->
                currentState.copy(founds = foundObjects)
            }
            _viewState.update { it.copy(isLoading = true) }
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
        viewModelScope.launch(Dispatchers.IO) {
            val (foundObjects, _) = apiRepository.getSearch(text, 1)
            _viewState.update { currentState ->
                currentState.copy(founds = foundObjects)
            }
        }
    }
}
