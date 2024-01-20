package com.example.whattowatch

import com.example.whattowatch.dataObjects.MovieInfo
import com.example.whattowatch.dto.CompanyInfoDTO
import com.example.whattowatch.dto.SingleGenreDTO
import com.example.whattowatch.dataObjects.UserMovie

data class MainViewState (
    val isLoading: Boolean= false,
    val selectedGenre: String = "",
    val movies: Map<String, List<MovieInfo>> = mapOf(),
    val moviesToLoad: Int = 15,
    val companies: List<CompanyInfoDTO> = listOf(),
    val genres: List<SingleGenreDTO> = listOf(),
    val seenMovies: List<UserMovie> = listOf(),
    val watchLaterMovies: List<UserMovie> = listOf(),
    val notInterestedMovies: List<UserMovie> = listOf(),
    val dialog: MainViewDialog = MainViewDialog.None
)