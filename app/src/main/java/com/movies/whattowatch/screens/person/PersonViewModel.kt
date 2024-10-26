package com.movies.whattowatch.screens.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.model.dataClasses.MovieInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonViewModel (
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository,
    savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
    private val _event = MutableSharedFlow<String>()
    private val _viewState = MutableStateFlow(PersonViewState())
    val viewState = _viewState.asStateFlow()
    private var personId: Int = savedStateHandle.get<String>("personID")?.toInt() ?: 1

    init {
        _viewState.update { it.copy(isLoadingCredits = true, isLoadingDetails = true) }
        viewModelScope.launch {
            val personDTO = apiRepository.getPersonDetails(personId = personId)
            _viewState.update { currentState ->
                currentState.copy(
                    personDTO = personDTO
                )
            }
            _viewState.update { it.copy(isLoadingDetails = false) }
        }
        viewModelScope.launch {
            val movies = apiRepository.getMovieCredits(personId)
            _viewState.update { currentState ->
                currentState.copy(
                    movies = movies,
                )
            }
            _viewState.update { it.copy(isLoadingCredits = false) }
        }.invokeOnCompletion {
            _viewState.value.movies.forEach {
                getProvider(it)
            }
        }
    }

    private fun getProvider(movie: MovieInfo) {
        viewModelScope.launch {
            val provider = apiRepository.getProviders(
                movie.id,
                movie.isMovie
            )
            val updatedMovies = _viewState.value.movies.map { m ->
                if (m.id == movie.id) {
                    val logoPaths = provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    m.copy(providerName = logoPaths)
                } else {
                    m
                }
            }

            _viewState.update { currentState ->
                currentState.copy(movies = updatedMovies)
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

        }
    }
}
