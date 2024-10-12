package com.movies.whattowatch.person

import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dto.PersonDTO

data class PersonViewState (
    val isLoadingCredits: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val movies: List<MovieInfo> = listOf(),
    val personDTO: PersonDTO = PersonDTO()
)
