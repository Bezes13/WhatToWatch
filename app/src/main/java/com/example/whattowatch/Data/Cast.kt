package com.example.whattowatch.Data


data class Credits (
    val cast: List<Cast>
)

data class Cast (
    val name: String,
    val profile_path: String
)
