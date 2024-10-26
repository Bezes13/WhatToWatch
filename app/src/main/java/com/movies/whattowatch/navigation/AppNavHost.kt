package com.movies.whattowatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.movies.whattowatch.manager.DetailsViewModelFactory
import com.movies.whattowatch.manager.MainViewModelFactory
import com.movies.whattowatch.manager.PersonViewModelFactory
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.screens.details.DetailsScreen
import com.movies.whattowatch.screens.details.DetailsViewModel
import com.movies.whattowatch.screens.main.MainScreen
import com.movies.whattowatch.screens.person.PersonScreen
import com.movies.whattowatch.screens.person.PersonViewModel
import com.movies.whattowatch.screens.provider.ProviderScreen
import com.movies.whattowatch.screens.provider.ProviderViewModel
import com.movies.whattowatch.screens.search.SearchScreen
import com.movies.whattowatch.screens.search.SearchViewModel
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
            NavigationItem.Details.route + "/{movieId}/{isMovie}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType },navArgument("isMovie") { type = NavType.StringType })
        ) {
            val detailsViewModel: DetailsViewModel = viewModel(
                factory = DetailsViewModelFactory(apiRepository)
            )
            DetailsScreen(navController::navigate, detailsViewModel)
        }
        composable(NavigationItem.Main.route + "/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })) {
            MainScreen(navController::navigate, viewModel(factory = MainViewModelFactory(ioDispatcher, apiRepository)))
        }
        composable(NavigationItem.Search.route) {
            SearchScreen(navController::navigate, SearchViewModel(ioDispatcher, apiRepository))
        }
        composable(NavigationItem.Provider.route) {
            ProviderScreen(navController::navigate, ProviderViewModel(ioDispatcher, apiRepository))
        }
        composable(NavigationItem.Person.route + "/{personID}",
            arguments = listOf(navArgument("personID") { type = NavType.StringType })) {
            val personViewModel: PersonViewModel = viewModel(
                factory = PersonViewModelFactory(ioDispatcher, apiRepository)
            )
            PersonScreen(navController::navigate, personViewModel)
        }
    }
}


