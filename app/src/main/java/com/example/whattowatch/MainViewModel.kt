package com.example.whattowatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.Data.UserMovie
import com.example.whattowatch.Managers.SharedPreferencesManager
import com.example.whattowatch.Repository.ApiRepository
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

    private val companies =
        listOf("Netflix", "Disney Plus", "Amazon Prime Video", "WOW", "Crunchyroll")

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
            }
        }
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

    fun getMovies(genre: Genre, page: Int = 1, extend: Boolean = false) {
        if (!extend && !_viewState.value.movies[genre.name].isNullOrEmpty()) {
            println("NullOrEmpty")
            return
        }
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val movieDTO = apiRepository.getMovies(page, genre, _viewState.value.companies)
            var filtered = movieDTO.results.filter {
                !_viewState.value.seenMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.watchLaterMovies.any { userMovie -> userMovie.movieId == it.id } && !_viewState.value.notInterestedMovies.any { userMovie -> userMovie.movieId == it.id } && !(_viewState.value.movies[genre.name]
                    ?: listOf()).any { movie -> movie.id == it.id }
            }
            var refreshedCount = 0
            while (filtered.count() <= 5) {
                val newMovies = apiRepository.getMovies(
                    page + refreshedCount, genre, _viewState.value.companies
                )
                refreshedCount++
                filtered = filtered + newMovies.results.filter {
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

    private suspend fun getCompanies() {
        val company = apiRepository.getCompanies()
        _viewState.update { currentState ->
            currentState.copy(companies = company.results.filter { companyInfo ->
                companies.contains(
                    companyInfo.provider_name
                )
            })
        }
    }

    private suspend fun getGenres() {
        val genres = apiRepository.getGenres()
        _viewState.update { currentState -> currentState.copy(genres = genres.genres) }
    }

    private fun getProvider(genre: String, movieID: Int) {
        //if(_viewState.value.movies[genre]?.first{ movieInfo -> movieID == movieInfo.id }?.provider_name?.isNotEmpty() == false){
        viewModelScope.launch(Dispatchers.IO) {
            val provider = apiRepository.getProviders(movieID)
            val updatedMovies = _viewState.value.movies[genre]?.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths =
                        provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
                    movie.copy(provider_name = logoPaths)
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

    fun saveSharedList(selectedGenre: String, newItem: Int, listID: Int) {
        val list = when (listID) {
            R.string.seen -> _viewState.value.seenMovies
            R.string.later -> _viewState.value.watchLaterMovies
            R.string.no -> _viewState.value.notInterestedMovies
            else -> listOf()
        }

        val updatedMovies = _viewState.value.movies[selectedGenre]?.filter { it.id != newItem }
        _viewState.update { currentState ->
            currentState.copy(movies = currentState.movies.toMutableMap().apply {
                this[selectedGenre] = updatedMovies.orEmpty()
            })
        }
        //sharedPreferencesManager.context.deviceId.toString()
        val myRef = database.getReference(
            readName(R.string.user_name) + sharedPreferencesManager.context.getString(
                listID
            )
        )
        val myList = if (list.any { userMovie -> userMovie.movieId == newItem }) list - UserMovie(newItem, readName(R.string.user_name)) else list + UserMovie(newItem, readName(R.string.user_name))

        when (listID) {
            R.string.seen -> _viewState.update { _viewState.value.copy(seenMovies = myList) }

            R.string.later -> _viewState.update { _viewState.value.copy(watchLaterMovies = myList) }

            R.string.no -> _viewState.update { _viewState.value.copy(notInterestedMovies = myList) }

            else -> {}
        }
        val gson = Gson()
        val json = gson.toJson(myList)
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
                val userMovies = result.map { id -> UserMovie(id, readName(R.string.user_name)) }
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
                val userMovies = result.map { id -> UserMovie(id, readName(R.string.friend_name)) }

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
        viewModelScope.launch(Dispatchers.IO) {
            if (extend) {
                newList.addAll((viewState.value.movies[customList] ?: listOf()))
            }
            var loadedMovies = 0
            list.forEach {
                if (loadedMovies <= 10 && (!extend || !(viewState.value.movies[customList]
                        ?: listOf()).any { m -> m.id == it.movieId })
                ) {
                    val movie = apiRepository.getMovieDetails(it.movieId)
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
    }

    fun checkFilm(genre: String, movieId: Int): Boolean {
        return !(_viewState.value.movies[genre]?: listOf()).any{info-> info.id == movieId}
    }
}

sealed class MainViewEvent {
    data class SetDialog(val dialog: MainViewDialog) : MainViewEvent()
}