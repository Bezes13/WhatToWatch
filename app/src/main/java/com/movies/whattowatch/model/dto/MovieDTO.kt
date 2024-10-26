package com.movies.whattowatch.model.dto

data class MovieDTO(
    val page: Int,
    val results: List<MovieInfoDTO>,
    val total_pages: Int,
    val total_results: Int
)