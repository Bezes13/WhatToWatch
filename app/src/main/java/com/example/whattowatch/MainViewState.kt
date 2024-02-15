package com.example.whattowatch

import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dataClasses.Provider
import com.example.whattowatch.enums.SortType
import com.example.whattowatch.dataClasses.UserMovie
import com.example.whattowatch.dataClasses.Genre

data class MainViewState (
    val isLoading: Boolean= false,
    val selectedGenre: String = Genre().name,
    val shows: Map<String, List<MovieInfo>> = mapOf(),
    val moviesToLoad: Int = 15,
    val providers: List<Provider> = listOf(),
    val genres: List<Genre> = listOf(),
    val seriesGenres: List<Genre> = listOf(),
    val markedShows: List<UserMovie> = listOf(),
    val dialog: MainViewDialog = MainViewDialog.None,
    val showMovies: Boolean = true,
    val sorting: SortType = SortType.POPULARITY
)