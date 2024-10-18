package com.movies.whattowatch

import com.movies.whattowatch.dataClasses.Genre
import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dataClasses.Provider
import com.movies.whattowatch.dataClasses.UserMovie
import com.movies.whattowatch.enums.MovieCategory
import com.movies.whattowatch.enums.SortType

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
    val sorting: SortType = SortType.POPULARITY,
    val loadMore: Boolean = true,
    val category: MovieCategory = MovieCategory.Movie
)
