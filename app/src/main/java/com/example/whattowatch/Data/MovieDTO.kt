package com.example.whattowatch.Data

data class MovieDTO(
    val page: Int,
    val results: List<MovieInfo>,
    val total_pages: Int,
    val total_results: Int
)