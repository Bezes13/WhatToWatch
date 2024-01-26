package com.example.whattowatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowatch.apiRepository.ApiRepository
import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dataClasses.UserMovie
import com.example.whattowatch.dto.CastDTO
import com.example.whattowatch.dto.SingleGenreDTO
import com.example.whattowatch.manager.SharedPreferencesManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val database = Firebase.database
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(MainViewState())
    val viewState = _viewState.asStateFlow()

    val markFilmAs = listOf(
        sharedPreferencesManager.context.getString(R.string.seen),
        sharedPreferencesManager.context.getString(R.string.later),
        sharedPreferencesManager.context.getString(R.string.no)
    )

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
        }
    }

    private fun readSeenMovieList() {
        readSharedList(R.string.seen)
        readSharedList(R.string.later)
        readSharedList(R.string.no)
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
            }
        }
    }

    private fun updateProvider(providerId: Int, useProvider: Boolean) {
        _viewState.update { currentState ->
            currentState.copy(companies = currentState.companies.toMutableList().map { provider ->
                if (provider.providerId == providerId) provider.copy(show = useProvider) else provider
            }, series = mapOf(), movies = mapOf())
        }

        if (_viewState.value.genres.any { it.name == _viewState.value.selectedGenre }) {
            getMovies(_viewState.value.genres.first { it.name == _viewState.value.selectedGenre })
        }

        if (_viewState.value.seriesGenres.any { it.name == _viewState.value.selectedGenre }) {
            getSeries(_viewState.value.seriesGenres.first { it.name == _viewState.value.selectedGenre })
        } else {
            getSeries(_viewState.value.seriesGenres.first())
        }


        sharedPreferencesManager.saveList(
            R.string.provider,
            _viewState.value.companies.filter { it.show }.map { it.providerId.toString() })
    }

    private fun changeIsMovie(isMovie: Boolean) {
        _viewState.update { it.copy(showMovies = isMovie) }
        if (isMovie) {
            if (_viewState.value.genres.any { it.name == _viewState.value.selectedGenre }) {
                getMovies(_viewState.value.genres.first { it.name == _viewState.value.selectedGenre })
            } else {
                getMovies(_viewState.value.genres.first())
            }
        } else {
            if (_viewState.value.seriesGenres.any { it.name == _viewState.value.selectedGenre }) {
                getSeries(_viewState.value.seriesGenres.first { it.name == _viewState.value.selectedGenre })
            } else {
                getSeries(_viewState.value.seriesGenres.first())
            }
        }
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
            getCustomList(genre, true)
        }
    }

    fun getMovies(genre: SingleGenreDTO, page: Int = 1, extend: Boolean = false) {
        if (!_viewState.value.showMovies) {
            getSeries(genre, page, extend)
            return
        }
        if (!extend && !_viewState.value.movies[genre.name].isNullOrEmpty()) {
            return
        }
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val movies = apiRepository.getMovies(page, genre, _viewState.value.companies, true)
            var filtered = movies.filter { movieInfo ->
                !_viewState.value.seenMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !_viewState.value.watchLaterMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !_viewState.value.notInterestedMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !(_viewState.value.movies[genre.name]
                    ?: listOf()).any { movie -> movie.id == movieInfo.id }
            }
            var refreshedCount = 0
            while (filtered.count() <= 5) {
                val newMovies = apiRepository.getMovies(
                    page + refreshedCount, genre, _viewState.value.companies, true
                )
                refreshedCount++
                filtered = filtered + newMovies.filter {
                    !_viewState.value.seenMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.watchLaterMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.notInterestedMovies.any { userMovie -> userMovie.movieId == it.id } && !(_viewState.value.movies[genre.name]
                        ?: listOf()).any { movie -> movie.id == it.id }
                }
            }
            if (extend) {
                val loadedMovies = (_viewState.value.movies[genre.name] ?: emptyList()) + filtered
                _viewState.update { currentState ->
                    currentState.copy(movies = currentState.movies.toMutableMap().apply {
                        this[genre.name] = loadedMovies
                    }, isLoading = false)
                }
            } else {
                _viewState.update { currentState ->
                    currentState.copy(movies = currentState.movies.toMutableMap().apply {
                        this[genre.name] = filtered
                    }, isLoading = false)
                }
            }

            if ((_viewState.value.movies[genre.name]?.count()
                    ?: 0) < _viewState.value.moviesToLoad
            ) {
                getMovies(genre, page + 1, true)
            }
        }.invokeOnCompletion {
            _viewState.value.movies[genre.name]?.forEach {
                getProvider(genre = genre.name, it.id)
            }
        }
    }

    private fun getSeries(genre: SingleGenreDTO, page: Int = 1, extend: Boolean = false) {
        if (!extend && !_viewState.value.series[genre.name].isNullOrEmpty()) {
            return
        }
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val movies = apiRepository.getMovies(page, genre, _viewState.value.companies, false)
            var filtered = movies.filter { movieInfo ->
                !_viewState.value.seenMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !_viewState.value.watchLaterMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !_viewState.value.notInterestedMovies.any { userMovie -> userMovie.movieId == movieInfo.id } && !(_viewState.value.series[genre.name]
                    ?: listOf()).any { movie -> movie.id == movieInfo.id }
            }
            var refreshedCount = 0
            while (filtered.count() <= 5) {
                val newMovies = apiRepository.getMovies(
                    page + refreshedCount, genre, _viewState.value.companies, false
                )
                refreshedCount++
                filtered = filtered + newMovies.filter {
                    !_viewState.value.seenMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.watchLaterMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.notInterestedMovies.any { userMovie -> userMovie.movieId == it.id } && !(_viewState.value.series[genre.name]
                        ?: listOf()).any { movie -> movie.id == it.id }
                }
            }
            if (extend) {
                val loadedMovies = (_viewState.value.series[genre.name] ?: emptyList()) + filtered
                _viewState.update { currentState ->
                    currentState.copy(series = currentState.series.toMutableMap().apply {
                        this[genre.name] = loadedMovies
                    }, isLoading = false)
                }
            } else {
                _viewState.update { currentState ->
                    currentState.copy(series = currentState.series.toMutableMap().apply {
                        this[genre.name] = filtered
                    }, isLoading = false)
                }
            }

            if ((_viewState.value.series[genre.name]?.count()
                    ?: 0) < _viewState.value.moviesToLoad
            ) {
                getMovies(genre, page + 1, true)
            }
        }.invokeOnCompletion {
            _viewState.value.series[genre.name]?.forEach {
                getSeriesProvider(genre = genre.name, it.id)
            }
        }
    }

    private suspend fun getCompanies() {
        val company =
            apiRepository.getCompanies(sharedPreferencesManager.getList(R.string.provider))
        _viewState.update { currentState ->
            currentState.copy(companies = company.filter { provider -> provider.priority != 999 }
                .sorted())
        }
        println(_viewState.value.companies)
    }

    fun getCast(movieInfo: MovieInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val cast = apiRepository.getCast(movieInfo.id, movieInfo.isMovie)
            val video = apiRepository.getVideo(movieInfo)

            _viewState.update { currentState ->
                currentState.copy(dialog = MainViewDialog.DetailsDialog(movieInfo, cast, video))
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
            val updatedMovies = _viewState.value.movies[genre]?.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths =
                        provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    movie.copy(providerName = logoPaths)
                } else {
                    movie
                }
            }

            _viewState.update { currentState ->
                currentState.copy(movies = currentState.movies.toMutableMap().apply {
                    this[genre] = updatedMovies.orEmpty()
                })
            }
        }
    }

    private fun getSeriesProvider(genre: String, movieID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val provider = apiRepository.getProviders(movieID, _viewState.value.showMovies)
            val updatedMovies = _viewState.value.series[genre]?.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths =
                        provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    movie.copy(providerName = logoPaths)
                } else {
                    movie
                }
            }

            _viewState.update { currentState ->
                currentState.copy(series = currentState.series.toMutableMap().apply {
                    this[genre] = updatedMovies.orEmpty()
                })
            }
        }
    }

    fun saveSharedList(selectedGenre: String, newItem: Int, listID: Int) {
        val list = when (listID) {
            R.string.seen -> _viewState.value.seenMovies
            R.string.later -> _viewState.value.watchLaterMovies
            R.string.no -> _viewState.value.notInterestedMovies
            else -> listOf()
        }
        if (_viewState.value.showMovies) {
            val updatedMovies = _viewState.value.movies[selectedGenre]?.filter { it.id != newItem }
            _viewState.update { currentState ->
                currentState.copy(movies = currentState.movies.toMutableMap().apply {
                    this[selectedGenre] = updatedMovies.orEmpty()
                })
            }
        } else {
            val updatedMovies = _viewState.value.series[selectedGenre]?.filter { it.id != newItem }
            _viewState.update { currentState ->
                currentState.copy(series = currentState.series.toMutableMap().apply {
                    this[selectedGenre] = updatedMovies.orEmpty()
                })
            }
        }

        val myRef = database.getReference(
            readName(R.string.user_name) + sharedPreferencesManager.context.getString(
                listID
            )
        )
        val myList = if (list.any { userMovie -> userMovie.movieId == newItem }) list - UserMovie(
            newItem,
            readName(R.string.user_name),
            _viewState.value.showMovies
        ) else list + UserMovie(newItem, readName(R.string.user_name), _viewState.value.showMovies)

        when (listID) {
            R.string.seen -> _viewState.update { _viewState.value.copy(seenMovies = myList) }

            R.string.later -> _viewState.update { _viewState.value.copy(watchLaterMovies = myList) }

            R.string.no -> _viewState.update { _viewState.value.copy(notInterestedMovies = myList) }

            else -> {}
        }
        val gson = Gson()
        val json = gson.toJson(myList.filter { movie -> movie.name == readName(R.string.user_name) }
            .map { userMovie -> if (userMovie.isMovie) userMovie.movieId else -userMovie.movieId })
        myRef.setValue(json)
    }

    fun saveName(name: String, saveId: Int) {
        sharedPreferencesManager.saveName(name, saveId)
    }

    fun readName(saveId: Int): String {
        return sharedPreferencesManager.readName(saveId)
    }

    private fun readSharedList(key: Int) {
        val type = object : TypeToken<List<Int>>() {}.type
        database.getReference(
            readName(R.string.user_name) + sharedPreferencesManager.context.getString(key)
        ).get().addOnSuccessListener { data ->
            if (data.value != null) {
                val result: List<Int> = Gson().fromJson(data.value.toString(), type)
                val userMovies =
                    result.map { id -> UserMovie(abs(id), readName(R.string.user_name), id > 0) }
                when (key) {
                    R.string.seen -> _viewState.update { _viewState.value.copy(seenMovies = userMovies) }

                    R.string.later -> _viewState.update { _viewState.value.copy(watchLaterMovies = userMovies) }

                    R.string.no -> _viewState.update { _viewState.value.copy(notInterestedMovies = userMovies) }

                    else -> {}
                }
            }
        }
        database.getReference(
            readName(R.string.friend_name) + sharedPreferencesManager.context.getString(key)
        ).get().addOnSuccessListener { data ->
            if (data.value != null) {
                val result: List<Int> = Gson().fromJson(data.value.toString(), type)
                val userMovies =
                    result.map { id -> UserMovie(id, readName(R.string.friend_name), id > 0) }

                when (key) {
                    R.string.seen -> _viewState.update { it.copy(seenMovies = it.seenMovies + userMovies) }

                    R.string.later -> _viewState.update { it.copy(watchLaterMovies = it.watchLaterMovies + userMovies) }

                    R.string.no -> _viewState.update { it.copy(notInterestedMovies = it.notInterestedMovies + userMovies) }

                    else -> {}
                }
            }
        }
    }

    fun getCustomList(customList: String, extend: Boolean = false) {
        val list = when (customList) {
            sharedPreferencesManager.context.getString(R.string.seen) -> _viewState.value.seenMovies
            sharedPreferencesManager.context.getString(R.string.later) -> _viewState.value.watchLaterMovies
            sharedPreferencesManager.context.getString(R.string.no) -> _viewState.value.notInterestedMovies
            else -> listOf()
        }

        val newList: ArrayList<MovieInfo> = arrayListOf()
        _viewState.update { it.copy(isLoading = true) }
        if (_viewState.value.showMovies) {
            viewModelScope.launch(Dispatchers.IO) {
                if (extend) {
                    newList.addAll((viewState.value.movies[customList] ?: listOf()))
                }
                var loadedMovies = 0
                list.filter { userMovie -> userMovie.isMovie }.forEach {
                    if (loadedMovies <= 10 && (!extend || !(viewState.value.movies[customList]
                            ?: listOf()).any { m -> m.id == it.movieId })
                    ) {
                        val movie =
                            apiRepository.getMovieDetails(it.movieId, _viewState.value.showMovies)
                        movie.user = it.name
                        newList.add(movie)
                        loadedMovies++
                    }
                }

                _viewState.update { currentState ->
                    currentState.copy(movies = currentState.movies.toMutableMap().apply {
                        this[customList] = newList
                    }, isLoading = false)
                }
            }.invokeOnCompletion {
                _viewState.value.movies[customList]?.forEach {
                    getProvider(genre = customList, it.id)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                if (extend) {
                    newList.addAll((viewState.value.series[customList] ?: listOf()))
                }
                var loadedMovies = 0
                list.filter { userMovie -> !userMovie.isMovie }.forEach {
                    if (loadedMovies <= 10 && (!extend || !(viewState.value.series[customList]
                            ?: listOf()).any { m -> m.id == it.movieId })
                    ) {
                        val movie =
                            apiRepository.getMovieDetails(it.movieId, _viewState.value.showMovies)
                        movie.user = it.name
                        newList.add(movie)
                        loadedMovies++
                    }
                }

                _viewState.update { currentState ->
                    currentState.copy(series = currentState.series.toMutableMap().apply {
                        this[customList] = newList
                    }, isLoading = false)
                }
            }.invokeOnCompletion {
                _viewState.value.series[customList]?.forEach {
                    getProvider(genre = customList, it.id)
                }
            }
        }

    }


}

sealed class MainViewEvent {
    data class SetDialog(val dialog: MainViewDialog) : MainViewEvent()
    data class SetGenre(val genre: String) : MainViewEvent()
    data class ChangeIsMovie(val isMovie: Boolean) : MainViewEvent()
    data class UpdateProvider(val providerId: Int, val useProvider: Boolean) : MainViewEvent()
}