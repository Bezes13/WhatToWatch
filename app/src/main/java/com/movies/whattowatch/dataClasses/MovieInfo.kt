package com.movies.whattowatch.dataClasses

import com.movies.whattowatch.enums.UserMark

data class MovieInfo(
    val id: Int = 0,
    val originalLanguage: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val posterPath: String = "",
    val releaseDate: String = "",
    val title: String = "",
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0,
    val providerName: List<String>? = listOf(),
    val link: String = "",
    val isMovie: Boolean = true,
    var user: String? = "",
    var mediaType: MediaType? = null,
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