package com.example.whattowatch.dto

data class MovieInfoDTO(
    val id: Int,
    val original_language: String,
    val overview: String,
    val popularity: Number,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: Number,
    val vote_count: Int,
)
