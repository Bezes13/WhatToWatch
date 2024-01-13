package com.example.whattowatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whattowatch.Repository.ApiRepository

class MainViewModelFactory(
    private val apiRepository: ApiRepository,
    private val sharedPreferencesManager: SharedPreferencesManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sharedPreferencesManager, apiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}