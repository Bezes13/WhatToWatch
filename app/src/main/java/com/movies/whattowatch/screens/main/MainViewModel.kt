package com.movies.whattowatch.screens.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.model.dataClasses.Genre
import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dataClasses.Provider
import com.movies.whattowatch.model.enums.MovieCategory
import com.movies.whattowatch.model.enums.SortType
import com.movies.whattowatch.model.enums.UserMark
import com.movies.whattowatch.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val apiRepository: ApiRepository,
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(MainViewState())
    val viewState = _viewState.asStateFlow()
    private var category: MovieCategory =
        MovieCategory.valueOf(savedStateHandle.get<String>("category")?.toString() ?: "")
    private var database = FirebaseRepository()
    private var providers = listOf<Provider>()

    init {
        listenToEvent()
        _viewState.update { it.copy(isLoading = true, category = category) }
        initViewModel()
        _viewState.update { it.copy(isLoading = false) }
    }

    private fun initViewModel() {
        viewModelScope.launch {
            fetchData()
        }.invokeOnCompletion {
            if (category == MovieCategory.Marked) {
                getCustomList(UserMark.SEEN)
            }else{
                getMovies()
            }
        }
    }


    fun sendEvent(event: MainViewEvent) {
        viewModelScope.launch(ioDispatcher) {
            _event.emit(event)
        }
    }

    private fun listenToEvent() = viewModelScope.launch(ioDispatcher) {
        _event.collect { event ->
            when (event) {
                is MainViewEvent.SetGenre -> updateGenre(event.genre)
                is MainViewEvent.ChangeSorting -> changeSorting(event.sortType)
                is MainViewEvent.MarkFilmAs -> saveSharedList(
                    event.newItem,
                    event.userMark
                )

                is MainViewEvent.FetchMovies -> getMovies()
                is MainViewEvent.UpdateLoadedMovies -> changeGenre()
                is MainViewEvent.ShowCustom -> getCustomList(event.userMark)
            }
        }
    }

    private fun changeSorting(sortType: SortType) {
        if (_viewState.value.sorting == sortType) {
            return
        }
        _viewState.update { state ->
            state.copy(
                sorting = sortType
            )
        }
        getMovies()
    }

    private fun updateGenre(genre: Genre) {
        _viewState.update { it.copy(selectedGenre = if (it.selectedGenre.contains(genre)) it.selectedGenre - genre else it.selectedGenre + genre) }
        getMovies()
    }

    private fun changeGenre() {
        getMovies()
    }

    private fun getMovies(page: Int = 1) {
        viewModelScope.launch {
            val movies = apiRepository.getMovies(
                page,
                viewState.value.selectedGenre,
                providers,
                category == MovieCategory.Movie,
                _viewState.value.sorting
            )
            if (movies.isEmpty()) {
                _viewState.update { it.copy(loadMore = false, isLoading = false) }
                return@launch
            }
            var filtered = movies.filter { movieInfo ->
                !_viewState.value.markedShows.any { userMovie -> userMovie.movieId == movieInfo.id }
            }
            var refreshedCount = 0
            while (filtered.count() <= 5) {
                val newMovies = apiRepository.getMovies(
                    page + refreshedCount,
                    viewState.value.selectedGenre,
                    providers,
                    category == MovieCategory.Movie,
                    _viewState.value.sorting
                )
                refreshedCount++
                filtered = filtered + newMovies.filter {
                    !_viewState.value.markedShows.any { userMovie -> userMovie.movieId == it.id }
                }
            }

            _viewState.update { currentState ->
                currentState.copy(shows = filtered)
            }

        }.invokeOnCompletion {
            _viewState.value.shows.forEach {
                getProvider(it.id)
            }
        }
    }


    private suspend fun fetchData() {
        val company = apiRepository.getCompanies(database.getProvider())
        providers = company.filter { provider -> provider.priority != 999 }.sorted()
        _viewState.update { currentState ->
            currentState.copy(
                seriesGenres = apiRepository.getGenres(false).genres,
                genres = apiRepository.getGenres(true).genres,
                markedShows = database.getUserMovies()
            )
        }
    }

    private fun getProvider( movieID: Int) {
        viewModelScope.launch {
            val provider = apiRepository.getProviders(
                movieID,
                category == MovieCategory.Movie
            )
            val updatedMovies = _viewState.value.shows.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths =
                        provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    movie.copy(providerName = logoPaths)
                } else {
                    movie
                }
            }

            _viewState.update { currentState ->
                currentState.copy(shows = updatedMovies)
            }
        }
    }

    private fun saveSharedList(newItem: MovieInfo, userMark: UserMark) {
        val updatedMovies = _viewState.value.shows.filter { it.id != newItem.id }
        val newUserMovie = newItem.convertToUserMovie(userMark)

        _viewState.update { currentState ->
            currentState.copy(
                shows = updatedMovies
            )
        }

        viewModelScope.launch {
            database.addOrUpdateUserMovie(newUserMovie)
        }
    }

    private fun getCustomList(customList: UserMark) {
        _viewState.update { state ->
            state.copy(
                selectedGenre = listOf(Genre(-2, customList.name)),
                shows = state.markedShows.filter { it.userMark == customList }
                        .map { it.convertToMovieInfo() }
                )
        }
    }
}

sealed class MainViewEvent {
    data class SetGenre(val genre: Genre) : MainViewEvent()
    data class ChangeSorting(val sortType: SortType) : MainViewEvent()
    data class MarkFilmAs(
        val newItem: MovieInfo,
        val userMark: UserMark
    ) : MainViewEvent()

    data class FetchMovies(val genre: Genre) : MainViewEvent()
    data object UpdateLoadedMovies : MainViewEvent()
    data class ShowCustom(val userMark: UserMark) : MainViewEvent()
}