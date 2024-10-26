package com.movies.whattowatch.screens.person

import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dto.PersonDTO

data class PersonViewState (
    val isLoadingCredits: Boolean = false,
    val isLoadingDetails: Boolean = false,
    val movies: List<MovieInfo> = listOf(),
    val personDTO: PersonDTO = PersonDTO()
)
