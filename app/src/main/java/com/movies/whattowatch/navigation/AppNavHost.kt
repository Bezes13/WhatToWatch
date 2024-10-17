package com.movies.whattowatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.movies.whattowatch.MainScreen
import com.movies.whattowatch.apiRepository.ApiRepository
import com.movies.whattowatch.details.DetailsScreen
import com.movies.whattowatch.details.DetailsViewModel
import com.movies.whattowatch.manager.MainViewModelFactory
import com.movies.whattowatch.person.PersonScreen
import com.movies.whattowatch.person.PersonViewModel
import com.movies.whattowatch.provider.ProviderScreen
import com.movies.whattowatch.provider.ProviderViewModel
import com.movies.whattowatch.search.SearchScreen
import com.movies.whattowatch.search.SearchViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
                factory = DetailsViewModelFactory(ioDispatcher, apiRepository)
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


class DetailsViewModelFactory(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle() // Retrieves SavedStateHandle from the creation extras
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(ioDispatcher, apiRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class PersonViewModelFactory(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle() // Retrieves SavedStateHandle from the creation extras
        if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
            return PersonViewModel(ioDispatcher, apiRepository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


