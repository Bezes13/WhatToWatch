package com.movies.whattowatch.provider

import com.movies.whattowatch.dataClasses.Provider

data class ProviderViewState(
    val isLoading: Boolean = true,
    val providers: List<Provider> = listOf(),
)
