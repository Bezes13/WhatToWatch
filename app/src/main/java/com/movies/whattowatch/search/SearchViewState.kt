package com.movies.whattowatch.search

import com.movies.whattowatch.dataClasses.MovieInfo

data class SearchViewState  (
    val isLoading: Boolean = true,
    val founds: List<MovieInfo> = listOf(),
)
