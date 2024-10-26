package com.movies.whattowatch.screens.provider

import com.movies.whattowatch.model.dataClasses.Provider

data class ProviderViewState(
    val isLoading: Boolean = true,
    val providers: List<Provider> = listOf(),
)
