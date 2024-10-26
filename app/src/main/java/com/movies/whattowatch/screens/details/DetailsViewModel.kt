package com.movies.whattowatch.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val apiRepository: ApiRepository,
    savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
    private val _viewState = MutableStateFlow(DetailsViewState())
    val viewState = _viewState.asStateFlow()
    private var movieId: Int = savedStateHandle.get<String>("movieId")?.toInt() ?: 1
    private var isMovie: Boolean = savedStateHandle.get<String>("isMovie")?.toBoolean() ?: false


    init {
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val cast = apiRepository.getCast(movieId, isMovie)
            val video = apiRepository.getVideo(movieId, isMovie)
            val provider = apiRepository.getProviders(movieId, isMovie)

            val newInfo = apiRepository.getMovieDetails(movieId, isMovie).copy(providerName = provider.results["DE"]?.flatrate?.map { it.logo_path })

            _viewState.update { currentState ->
                currentState.copy(info = newInfo, cast = cast, videos = video)
            }
            _viewState.update { it.copy(isLoading = false) }
        }
    }
}
