package com.movies.whattowatch.navigation

enum class Screen {
    SEARCH,
    MAIN,
    DETAILS,
    PROVIDER
}

sealed class NavigationItem(val route: String) {
    data object Main : NavigationItem(Screen.MAIN.name)
    data object Search : NavigationItem(Screen.SEARCH.name)
    data object Provider : NavigationItem(Screen.PROVIDER.name)
    data object Details : NavigationItem(Screen.DETAILS.name)
}