package com.movies.whattowatch.model.dto

data class PersonDTO(
    val birthday: String = "",
    val deathday: String? = null,
    val id: Int = 0,
    val name: String = "",
    val profile_path: String = ""
)
