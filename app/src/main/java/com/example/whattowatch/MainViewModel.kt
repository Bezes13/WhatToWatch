package com.example.whattowatch

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.Repository.ApiRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private var sharedPreferencesManager: SharedPreferencesManager,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    private val database = Firebase.database

    private val _viewState = MutableStateFlow(MainViewState())
    val viewState = _viewState.asStateFlow()

    val markFilmAs = listOf("Gesehen", "Später Ansehen", "Nicht Interessiert")
    private val companies = listOf("Netflix", "Disney Plus", "Amazon Prime Video", "WOW", "Crunchyroll")

    init {
        initViewModel()
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
    }

    fun getMovies(genre: Genre, page: Int = 1, extend: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val movieDTO = apiRepository.getMovies(page, genre, _viewState.value.companies)
            val filtered = movieDTO.results.filter {
                !_viewState.value.seenMovies.contains(
                    it.id
                )
            }
            if (extend) {
                val loadedMovies =
                    (_viewState.value.movies[genre.name] ?: emptyList()) + filtered
                _viewState.update { currentState ->
                    currentState.copy(movies = currentState.movies.toMutableMap().apply {
                        this[genre.name] = loadedMovies
                    })
                }
            } else {
                _viewState.update { currentState ->
                    currentState.copy(movies = currentState.movies.toMutableMap().apply {
                        this[genre.name] = filtered
                    })
                }
            }

            if ((_viewState.value.movies[genre.name]?.count()
                    ?: 0) < _viewState.value.moviesToLoad
            ) {
                getMovies(genre, page + 1, true)
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

    fun getProvider(genre: String, movieID: Int) {
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
        val updatedMovies = _viewState.value.movies[selectedGenre]?.filter { it.id != newItem }
        _viewState.update { currentState ->
            currentState.copy(movies = currentState.movies.toMutableMap().apply {
                this[selectedGenre] = updatedMovies.orEmpty()
            })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val myRef = database.getReference(
                sharedPreferencesManager.context.deviceId.toString() + sharedPreferencesManager.context.getString(
                    listID
                )
            )
            val myList = _viewState.value.seenMovies + newItem
            _viewState.update { _viewState.value.copy(seenMovies = myList) }
            val gson = Gson()
            val json = gson.toJson(myList)
            myRef.setValue(json)
        }
    }


    private fun readSharedList(key: Int) {
        val type = object : TypeToken<List<Int>>() {}.type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            database.getReference(
                sharedPreferencesManager.context.deviceId.toString() + sharedPreferencesManager.context.getString(
                    key
                )
            ).get().addOnSuccessListener { data ->
                if (data.value != null) _viewState.update {
                    _viewState.value.copy(
                        seenMovies = Gson().fromJson(
                            data.value.toString(), type
                        )
                    )
                }
            }
        }
    }

    fun getCustomList(customList: String) {
        val list = when (customList) {
            "Gesehen" -> _viewState.value.seenMovies
            else -> listOf()
        }
        val newList: ArrayList<MovieInfo> = arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {

                list.forEach {
                    newList.add(apiRepository.getMovieDetails(it))
                }

            _viewState.update { currentState ->
                currentState.copy(movies = currentState.movies.toMutableMap().apply {
                    this[customList] = newList
                })
            }
        }
    }
}