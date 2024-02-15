package com.example.whattowatch.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whattowatch.MainViewModel
import com.example.whattowatch.apiRepository.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelFactory(
    private val apiRepository: ApiRepository,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sharedPreferencesManager, apiRepository, ioDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}