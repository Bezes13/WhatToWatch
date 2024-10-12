package com.movies.whattowatch.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.movies.whattowatch.MainViewModel
import com.movies.whattowatch.apiRepository.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelFactory(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle() // Retrieves SavedStateHandle from the creation extras
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiRepository, ioDispatcher, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}