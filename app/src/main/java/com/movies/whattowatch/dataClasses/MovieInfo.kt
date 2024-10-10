package com.movies.whattowatch.dataClasses

import com.movies.whattowatch.enums.UserMark

data class MovieInfo(
    val id: Int,
    val originalLanguage: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
    val providerName: List<String>? = listOf(),
    val isMovie: Boolean,
    var user: String? = "",
    var mediaType: MediaType?,
    var knownFor: List<MovieInfo>? = listOf()
) {
    fun convertToUserMovie(userMark: UserMark): UserMovie {
        return UserMovie(
            movieId = id,
            userMark = userMark,
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

enum class MediaType {
    PERSON, TV, MOVIE
}