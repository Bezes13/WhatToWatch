package com.movies.whattowatch.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.screens.person.PersonViewModel
import kotlinx.coroutines.CoroutineDispatcher

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