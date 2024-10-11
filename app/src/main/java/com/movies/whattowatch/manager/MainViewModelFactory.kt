package com.movies.whattowatch.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movies.whattowatch.MainViewModel
import com.movies.whattowatch.apiRepository.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelFactory(
    private val apiRepository: ApiRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiRepository, ioDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}