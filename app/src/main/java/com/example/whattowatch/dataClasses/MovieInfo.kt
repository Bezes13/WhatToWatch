package com.example.whattowatch.dataClasses

data class MovieInfo(
    val id: Int,
    val originalLanguage: String,
    val overview: String,
    val popularity: Number,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val voteAverage: Number,
    val voteCount: Int,
    val providerName: List<String>? = listOf(),
    val isMovie: Boolean,
    var user: String? = "",
)