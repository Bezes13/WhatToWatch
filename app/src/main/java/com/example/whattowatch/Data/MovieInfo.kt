package com.example.whattowatch.Data

data class MovieInfo(
    // TODO add a new MovieDTO class for custom Lists
    //val genre_ids: List<Int> = listOf(),
    val id: Int,
    val original_language: String,
    val overview: String,
    val popularity: Number,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: Number,
    val vote_count: Int,
    val provider_name: List<String>? = listOf(),
    var user: String? = "",
    val cast: List<String>? = listOf()
)
