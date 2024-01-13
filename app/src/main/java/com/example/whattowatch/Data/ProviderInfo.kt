package com.example.whattowatch.Data

data class MovieAvailability(
    val id: Int,
    val results: Map<String, RegionDetails>
)

data class RegionDetails(
    val link: String,
    val flatrate: List<FlatrateProvider>
)

data class FlatrateProvider(
    val logo_path: String,
    val provider_id: Int,
    val provider_name: String
)

