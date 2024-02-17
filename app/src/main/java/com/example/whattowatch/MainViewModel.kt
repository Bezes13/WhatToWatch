package com.example.whattowatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowatch.apiRepository.ApiRepository
import com.example.whattowatch.dataClasses.Genre
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dataClasses.UserMovie
import com.example.whattowatch.dto.CastDTO
import com.example.whattowatch.enums.SortType
import com.example.whattowatch.enums.UserMark
import com.example.whattowatch.manager.SharedPreferencesManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainViewModel(
    private var sharedPreferencesManager: SharedPreferencesManager,
    private val apiRepository: ApiRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(MainViewState())
    val viewState = _viewState.asStateFlow()

    init {
        _viewState.update { it.copy(isLoading = true) }
        initViewModel()
        listenToEvent()
        _viewState.update { it.copy(isLoading = false) }
    }

    private fun initViewModel() {
        viewModelScope.launch {
            getCompanies()
            getGenres()
            readSeenMovieList()
            getMovies(Genre())
        }
    }

    private fun readSeenMovieList() {
        UserMark.entries.forEach {
            readSharedList(it)
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
                is MainViewEvent.SetDialog -> updateDialog(event.dialog)
                is MainViewEvent.SetGenre -> updateGenre(event.genre)
                is MainViewEvent.ChangeIsMovie -> changeIsMovie(event.isMovie)
                is MainViewEvent.UpdateProvider -> updateProvider(
                    event.providerId,
                    event.useProvider
                )

                is MainViewEvent.ChangeSorting -> changeSorting(event.sortType)
                is MainViewEvent.MarkFilmAs -> saveSharedList(
                    event.selectedGenre,
                    event.newItem,
                    event.userMark
                )
            }
        }
    }

    private fun changeSorting(sortType: SortType) {
        if (_viewState.value.sorting == sortType) {
            return
        }
        _viewState.update { state ->
            state.copy(
                sorting = sortType,
                shows = state.shows.filter {
                    UserMark.entries.map { mark -> mark.name }.contains(it.key)
                })
        }
        getMovies(viewState.value.genres.firstOrNull { it.name == _viewState.value.selectedGenre }
            ?: Genre())
    }

    private fun updateProvider(providerId: Int, useProvider: Boolean) {
        _viewState.update { currentState ->
            currentState.copy(
                providers = currentState.providers.toMutableList().map { provider ->
                    if (provider.providerId == providerId) provider.copy(show = useProvider) else provider
                },
                shows = currentState.shows.filter {
                    UserMark.entries.map { mark -> mark.name }.contains(it.key)
                })
        }

        getMovies(viewState.value.genres.firstOrNull { it.name == _viewState.value.selectedGenre }
            ?: Genre())

        sharedPreferencesManager.saveList(
            R.string.provider,
            _viewState.value.providers.filter { it.show }.map { it.providerId.toString() })
    }

    private fun changeIsMovie(isMovie: Boolean) {
        _viewState.update { state ->
            state.copy(
                showMovies = isMovie,
                shows = mapOf()
            )
        }
        getMovies(viewState.value.genres.firstOrNull { it.name == _viewState.value.selectedGenre }
            ?: Genre())
    }

    private fun updateGenre(genre: String) {
        _viewState.update { it.copy(selectedGenre = genre) }
    }

    private fun updateDialog(dialog: MainViewDialog) {
        _viewState.update { it.copy(dialog = dialog) }
    }

    fun changeLoadedMovies(genre: String) {
        val foundGenre = _viewState.value.genres.firstOrNull { g -> g.name == genre }
        if (foundGenre != null) {
            getMovies(foundGenre, extend = true)
        } else {
            getCustomList(UserMark.valueOf(_viewState.value.selectedGenre), extend = true)
        }
    }

    fun getMovies(genre: Genre, page: Int = 1, extend: Boolean = false) {
        if (!extend && (!_viewState.value.shows[genre.name].isNullOrEmpty())) {
            return
        }
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val movies = apiRepository.getMovies(
                page,
                genre,
                _viewState.value.providers,
                _viewState.value.showMovies,
                _viewState.value.sorting
            )
            if(movies.isEmpty()){
                _viewState.update { it.copy(loadMore = false, isLoading = false) }
                return@launch
            }
            var filtered = movies.filter { movieInfo ->
                !_viewState.value.markedShows.any { userMovie -> userMovie.movieId == movieInfo.id } && !(_viewState.value.shows[genre.name]
                    ?: listOf()).any { movie -> movie.id == movieInfo.id }
            }
            var refreshedCount = 0
            while (filtered.count() <= 5) {
                val newMovies = apiRepository.getMovies(
                    page + refreshedCount,
                    genre,
                    _viewState.value.providers,
                    _viewState.value.showMovies,
                    _viewState.value.sorting
                )
                refreshedCount++
                filtered = filtered + newMovies.filter {
                    !_viewState.value.markedShows.any { userMovie -> userMovie.movieId == it.id } && !(_viewState.value.shows[genre.name]
                        ?: listOf()).any { movie -> movie.id == it.id }
                }
            }
            if (extend) {
                val loadedMovies = (_viewState.value.shows[genre.name] ?: emptyList()) + filtered
                _viewState.update { currentState ->
                    currentState.copy(shows = currentState.shows.toMutableMap().apply {
                        this[genre.name] = loadedMovies
                    }, isLoading = false)
                }
            } else {
                _viewState.update { currentState ->
                    currentState.copy(shows = currentState.shows.toMutableMap().apply {
                        this[genre.name] = filtered
                    }, isLoading = false)
                }
            }

            if ((_viewState.value.shows[genre.name]?.count()
                    ?: 0) < _viewState.value.moviesToLoad
            ) {
                getMovies(genre, page + 1, true)
            }
        }.invokeOnCompletion {
            _viewState.value.shows[genre.name]?.forEach {
                getProvider(genre = genre.name, it.id)
            }
        }
    }

    private suspend fun getCompanies() {
        val company =
            apiRepository.getCompanies(sharedPreferencesManager.getList(R.string.provider))
        _viewState.update { currentState ->
            currentState.copy(providers = company.filter { provider -> provider.priority != 999 }
                .sorted())
        }
    }

    fun getCast(movieInfo: MovieInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val cast = apiRepository.getCast(movieInfo.id, movieInfo.isMovie)
            val video = apiRepository.getVideo(movieInfo)
            val provider = apiRepository.getProviders(movieInfo.id, _viewState.value.showMovies)
            val newInfo = movieInfo.copy(providerName =  provider.results["DE"]?.flatrate?.map { it.logo_path })
            _viewState.update { currentState ->
                currentState.copy(dialog = MainViewDialog.DetailsDialog(newInfo, cast, video))
            }
        }
    }

    fun getCredits(castDTO: CastDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            val cast = apiRepository.getMovieCredits(castDTO.id)

            _viewState.update { currentState ->
                currentState.copy(dialog = MainViewDialog.PersonDetails(castDTO.copy(credits = cast)))
            }
        }
    }

    private suspend fun getGenres() {
        val genres = apiRepository.getGenres(true)
        _viewState.update { currentState -> currentState.copy(genres = genres.genres) }
        val tvGenres = apiRepository.getGenres(false)
        _viewState.update { currentState -> currentState.copy(seriesGenres = tvGenres.genres) }
    }

    private fun getProvider(genre: String, movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val provider = apiRepository.getProviders(movieID, _viewState.value.showMovies)
            val updatedMovies = _viewState.value.shows[genre]?.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths =
                        provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    movie.copy(providerName = logoPaths)
                } else {
                    movie
                }
            }

            _viewState.update { currentState ->
                currentState.copy(shows = currentState.shows.toMutableMap().apply {
                    this[genre] = updatedMovies.orEmpty()
                })
            }
        }
    }

    private fun saveSharedList(selectedGenre: String, newItem: Int, userMark: UserMark) {

        val updatedMovies = _viewState.value.shows[selectedGenre]?.filter { it.id != newItem }
        _viewState.update { currentState ->
            currentState.copy(shows = currentState.shows.toMutableMap().apply {
                this[selectedGenre] = updatedMovies.orEmpty()
            })
        }


        val newUserMovie =
            UserMovie(newItem, "User", _viewState.value.showMovies, userMark)
        if (_viewState.value.markedShows.any { userMovie -> userMovie.movieId == newItem && userMark == userMovie.userMark })
            _viewState.update { it.copy(markedShows = it.markedShows - newUserMovie) } else
            _viewState.update { it.copy(markedShows = it.markedShows + newUserMovie) }

        val list =
            _viewState.value.markedShows.filter { movie -> movie.name == "User" && movie.userMark == userMark }
                .map { userMovie -> if (userMovie.isMovie) userMovie.movieId.toString() else (-userMovie.movieId).toString() }
        sharedPreferencesManager.saveList(userMark.textID, list)
    }

    private fun readSharedList(userMark: UserMark) {
        val list = sharedPreferencesManager.getList(userMark.textID).map { id ->
            UserMovie(
                abs(id.toInt()),
                "user",
                id.toInt() > 0,
                userMark
            )
        }

        _viewState.update { it.copy(markedShows = it.markedShows + list) }
    }


    fun getCustomList(customList: UserMark, extend: Boolean = false) {
        val newList: ArrayList<MovieInfo> = arrayListOf()
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            if (extend) {
                newList.addAll((viewState.value.shows[customList.toString()] ?: listOf()))
            }
            var loadedMovies = 0
            var loadMore = true
            val userList = _viewState.value.markedShows.filter { userMovie -> userMovie.isMovie && userMovie.userMark == customList }
                userList.forEach { userMovie ->
                    if (loadedMovies <= 10 && (!extend || !(viewState.value.shows[customList.toString()]
                            ?: listOf()).any { m -> m.id == userMovie.movieId })
                    ) {
                        val movie =
                            apiRepository.getMovieDetails(
                                userMovie.movieId,
                                _viewState.value.showMovies
                            )
                        movie.user = userMovie.name
                        newList.add(movie)
                        loadedMovies++
                        loadMore =  userList.last() != userMovie
                    }
                }
            _viewState.update { state -> state.copy(loadMore = loadMore) }

            _viewState.update { currentState ->
                currentState.copy(shows = currentState.shows.toMutableMap().apply {
                    this[customList.toString()] = newList
                }, isLoading = false)
            }
        }.invokeOnCompletion {
            _viewState.value.shows[customList.toString()]?.forEach {
                getProvider(genre = customList.toString(), it.id)
            }
        }
    }
}

sealed class MainViewEvent {
    data class SetDialog(val dialog: MainViewDialog) : MainViewEvent()
    data class SetGenre(val genre: String) : MainViewEvent()
    data class ChangeIsMovie(val isMovie: Boolean) : MainViewEvent()
    data class UpdateProvider(val providerId: Int, val useProvider: Boolean) : MainViewEvent()
    data class ChangeSorting(val sortType: SortType) : MainViewEvent()
    data class MarkFilmAs(val selectedGenre: String, val newItem: Int, val userMark: UserMark) :
        MainViewEvent()
}