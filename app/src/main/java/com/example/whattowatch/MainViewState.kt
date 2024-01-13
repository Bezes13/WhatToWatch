package com.example.whattowatch

import com.example.whattowatch.Data.CompanyInfo
import com.example.whattowatch.Data.Genre
import com.example.whattowatch.Data.MovieInfo

data class MainViewState (
    val movies: Map<String, List<MovieInfo>> = mapOf(),
    val moviesToLoad: Int = 15,
    val companies: List<CompanyInfo> = listOf(),
    val genres: List<Genre> = listOf(),
    val seenMovies: List<Int> = listOf(),
    val watchLaterMovies: List<Int> = listOf()
)