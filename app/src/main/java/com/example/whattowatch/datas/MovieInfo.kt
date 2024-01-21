package com.example.whattowatch.datas

import com.example.whattowatch.dto.CastDTO

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
    var user: String? = "",
    val cast: List<CastDTO>? = listOf()
)