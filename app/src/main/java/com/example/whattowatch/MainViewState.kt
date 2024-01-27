package com.example.whattowatch

import com.example.whattowatch.dataClasses.MovieInfo
import com.example.whattowatch.dataClasses.Provider
import com.example.whattowatch.dataClasses.SortType
import com.example.whattowatch.dataClasses.UserMovie
import com.example.whattowatch.dataClasses.Genre

data class MainViewState (
    val isLoading: Boolean= false,
    val selectedGenre: String = "No Genre",
    val movies: Map<String, List<MovieInfo>> = mapOf(),
    val series: Map<String, List<MovieInfo>> = mapOf(),
    val moviesToLoad: Int = 15,
    val providers: List<Provider> = listOf(),
    val genres: List<Genre> = listOf(),
    val seriesGenres: List<Genre> = listOf(),
    val seenMovies: List<UserMovie> = listOf(),
    val watchLaterMovies: List<UserMovie> = listOf(),
    val notInterestedMovies: List<UserMovie> = listOf(),
    val dialog: MainViewDialog = MainViewDialog.None,
    val showMovies: Boolean = true,
    val sorting: SortType = SortType.POPULARITY
)