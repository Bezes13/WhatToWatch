package com.movies.whattowatch.screens.details

import com.movies.whattowatch.model.dataClasses.MovieInfo
import com.movies.whattowatch.model.dto.CastDTO
import com.movies.whattowatch.model.dto.VideoInfoDTO

data class DetailsViewState (
    val isLoading: Boolean = true,
    val info: MovieInfo = MovieInfo(),
    val cast: List<CastDTO> = listOf(),
    val videos: List<VideoInfoDTO> = listOf(),
)
