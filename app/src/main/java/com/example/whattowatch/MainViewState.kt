package com.example.whattowatch

import com.example.whattowatch.Data.CompanyInfo
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo
import com.example.whattowatch.Data.UserMovie

data class MainViewState (
    val isLoading: Boolean= false,
    val selectedGenre: String = "",
    val movies: Map<String, List<MovieInfo>> = mapOf(),
    val moviesToLoad: Int = 15,
    val companies: List<CompanyInfo> = listOf(),
    val genres: List<Genre> = listOf(),
    val seenMovies: List<UserMovie> = listOf(),
    val watchLaterMovies: List<UserMovie> = listOf(),
    val notInterestedMovies: List<UserMovie> = listOf(),
    val dialog: MainViewDialog = MainViewDialog.None
)