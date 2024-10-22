package com.movies.whattowatch.dto

data class MovieAvailability(
    val id: Int,
    val results: Map<String, RegionDetails>
)

data class RegionDetails(
    val flatrate: List<FlatrateProvider>,
    val buy: List<FlatrateProvider>
)

data class FlatrateProvider(
    val logo_path: String,
    val provider_id: Int,
    val provider_name: String
)

