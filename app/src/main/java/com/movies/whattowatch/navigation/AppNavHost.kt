package com.movies.whattowatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.movies.whattowatch.MainScreen
import com.movies.whattowatch.MainViewModel
import com.movies.whattowatch.apiRepository.ApiRepository
import com.movies.whattowatch.details.DetailsScreen
import com.movies.whattowatch.details.DetailsViewModel
import com.movies.whattowatch.provider.ProviderScreen
import com.movies.whattowatch.provider.ProviderViewModel
import com.movies.whattowatch.search.SearchScreen
import com.movies.whattowatch.search.SearchViewModel
import kotlinx.coroutines.Dispatchers


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Main.route,
) {
    val apiRepository = ApiRepository(LocalContext.current)
    val ioDispatcher = Dispatchers.IO
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(
            NavigationItem.Main.route + "/{levelIndex}",
            arguments = listOf(navArgument("levelIndex") { type = NavType.StringType })
        ) {
            DetailsScreen(navController::navigate, DetailsViewModel())
        }
        composable(NavigationItem.Details.route) {
            MainScreen(navController::navigate, MainViewModel(apiRepository, ioDispatcher))
        }
        composable(NavigationItem.Search.route) {
            SearchScreen(navController::navigate, SearchViewModel())
        }
        composable(NavigationItem.Provider.route) {
            ProviderScreen(navController::navigate, ProviderViewModel())
        }
    }
}