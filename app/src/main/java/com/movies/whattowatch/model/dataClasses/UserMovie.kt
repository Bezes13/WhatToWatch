package com.movies.whattowatch.model.dataClasses

import com.movies.whattowatch.model.enums.UserMark

data class UserMovie(
    val movieId: Int = 0,
    val isMovie: Boolean = true,
    val userMark: UserMark = UserMark.SEEN,
    val originalLanguage: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val posterPath: String = "",
    val releaseDate: String = "",
    val title: String = "",
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0,
    val providerName: List<String>? = listOf(),
    var mediaType: MediaType? = MediaType.MOVIE,
){
    fun convertToMovieInfo(): MovieInfo {
        return MovieInfo(
            id = movieId,
            originalLanguage = originalLanguage,
            overview = overview,
            popularity = popularity,
            posterPath = posterPath,
            releaseDate = releaseDate,
            title = title,
            voteAverage = voteAverage,
            voteCount = voteCount,
            providerName = providerName,
            mediaType = mediaType,
            isMovie = isMovie
        )
    }
}