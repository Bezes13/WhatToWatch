package com.movies.whattowatch.screens.main

import com.movies.whattowatch.model.dataClasses.Genre
import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dataClasses.UserMovie
import com.movies.whattowatch.model.enums.MovieCategory
import com.movies.whattowatch.model.enums.SortType

data class MainViewState (
    val isLoading: Boolean= false,
    val selectedGenre: List<Genre> = listOf(),
    val shows: List<MovieInfo> = listOf(),
    val moviesToLoad: Int = 15,
    val genres: List<Genre> = listOf(),
    val seriesGenres: List<Genre> = listOf(),
    val markedShows: List<UserMovie> = listOf(),
    val sorting: SortType = SortType.POPULARITY,
    val loadMore: Boolean = true,
    val category: MovieCategory = MovieCategory.Movie
)
