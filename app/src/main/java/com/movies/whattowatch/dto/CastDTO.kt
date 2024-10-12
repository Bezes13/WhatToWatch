package com.movies.whattowatch.dto

import com.movies.whattowatch.dataClasses.MovieInfo

data class CastDTO (
    val name: String = "",
    val profile_path: String="",
    val id: Int=0,
    val credits: List<MovieInfo> = listOf()
)
