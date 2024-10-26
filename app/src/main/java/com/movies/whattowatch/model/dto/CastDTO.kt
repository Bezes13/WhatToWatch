package com.movies.whattowatch.model.dto

import com.movies.whattowatch.model.dataClasses.MovieInfo

data class CastDTO (
    val name: String = "",
    val profile_path: String="",
    val id: Int=0,
    val credits: List<MovieInfo> = listOf(),
    val character: String = ""
)
