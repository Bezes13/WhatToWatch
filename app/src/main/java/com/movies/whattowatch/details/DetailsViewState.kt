package com.movies.whattowatch.details

import com.movies.whattowatch.dataClasses.MovieInfo
import com.movies.whattowatch.dto.CastDTO
import com.movies.whattowatch.dto.VideoInfoDTO

data class DetailsViewState (
    val isLoading: Boolean = true,
    val info: MovieInfo = MovieInfo(),
    val cast: List<CastDTO> = listOf(),
    val videos: List<VideoInfoDTO> = listOf(),
)
