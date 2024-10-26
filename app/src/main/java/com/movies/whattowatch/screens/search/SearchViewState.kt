package com.movies.whattowatch.screens.search

import com.movies.whattowatch.model.dataClasses.MovieInfo

data class SearchViewState  (
    val isLoading: Boolean = true,
    val founds: List<MovieInfo> = listOf(),
)
