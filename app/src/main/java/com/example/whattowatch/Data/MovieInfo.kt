package com.example.whattowatch.Data

data class MovieInfo(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Number,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Number,
    val vote_count: Int,
    var provider_name: List<String> = listOf()
) {

}
