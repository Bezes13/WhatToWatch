package com.example.whattowatch

import android.content.Context
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

    val markFilmAs = listOf(
        sharedPreferencesManager.context.getString(R.string.seen),
        sharedPreferencesManager.context.getString(R.string.later),
        sharedPreferencesManager.context.getString(R.string.no)
    )
    private val companies =
        listOf("Netflix", "Disney Plus", "Amazon Prime Video", "WOW", "Crunchyroll")

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
        readSharedList(R.string.later)
        readSharedList(R.string.no)
    }

    fun getMovies(genre: Genre, page: Int = 1, extend: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val movieDTO = apiRepository.getMovies(page, genre, _viewState.value.companies)
            val filtered = movieDTO.results.filter {
                !_viewState.value.seenMovies.contains(it.id) &&
                        !_viewState.value.watchLaterMovies.contains(it.id) &&
                        !_viewState.value.notInterestedMovies.contains(it.id)
            }
            if (extend) {
                val loadedMovies = (_viewState.value.movies[genre.name] ?: emptyList()) + filtered
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
        }.invokeOnCompletion {
            _viewState.value.movies[genre.name]?.forEach {
            getProvider(genre = genre.name, it.id)
        } }


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
        //if(_viewState.value.movies[genre]?.first{ movieInfo -> movieID == movieInfo.id }?.provider_name?.isNotEmpty() == false){
        viewModelScope.launch(Dispatchers.IO) {
            val provider = apiRepository.getProviders(movieID)
            val updatedMovies = _viewState.value.movies[genre]?.map { movie ->
                if (movie.id == movieID) {
                    val logoPaths = provider.results["DE"]?.flatrate?.map { it.logo_path } ?: listOf()
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
        if (list.contains(newItem)) {
            return
        }
        // filter the seen Movie out of the shown ones
        val updatedMovies = _viewState.value.movies[selectedGenre]?.filter { it.id != newItem }
        _viewState.update { currentState ->
            currentState.copy(movies = currentState.movies.toMutableMap().apply {
                this[selectedGenre] = updatedMovies.orEmpty()
            })
        }
        //sharedPreferencesManager.context.deviceId.toString()
            val myRef = database.getReference(
                readName() + sharedPreferencesManager.context.getString(
                    listID
                )
            )
            val myList = list + newItem
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

    fun saveName(name: String){
        sharedPreferencesManager.saveName(name)
    }

    fun readName(): String{
        return sharedPreferencesManager.readName()
    }

    private fun readSharedList(key: Int) {
        val type = object : TypeToken<List<Int>>() {}.type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            database.getReference(readName() + sharedPreferencesManager.context.getString(key)
            ).get().addOnSuccessListener { data ->
                if (data.value != null) {
                    val result: List<Int> = Gson().fromJson(data.value.toString(), type)
                    when (key) {
                        R.string.seen -> _viewState.update { _viewState.value.copy(seenMovies = result) }

                        R.string.later -> _viewState.update { _viewState.value.copy(watchLaterMovies = result) }

                        R.string.no -> _viewState.update { _viewState.value.copy(notInterestedMovies = result) }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getCustomList(customList: String) {
        val list = when (customList) {
            sharedPreferencesManager.context.getString(R.string.seen) -> _viewState.value.seenMovies
            sharedPreferencesManager.context.getString(R.string.later) -> _viewState.value.watchLaterMovies
            sharedPreferencesManager.context.getString(R.string.no) -> _viewState.value.notInterestedMovies
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
        }.invokeOnCompletion { _viewState.value.movies[customList]?.forEach {
            getProvider(genre = customList, it.id)
        } }

    }
}