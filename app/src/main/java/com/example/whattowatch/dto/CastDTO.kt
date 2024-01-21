package com.example.whattowatch.dto

import com.example.whattowatch.datas.MovieInfo

data class CastDTO (
    val name: String,
    val profile_path: String,
    val id: Int,
    val credits: List<MovieInfo>
)
