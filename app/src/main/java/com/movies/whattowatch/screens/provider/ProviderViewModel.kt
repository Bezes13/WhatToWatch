package com.movies.whattowatch.screens.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.repository.FirebaseRepository
import com.movies.whattowatch.repository.ApiRepository
import com.movies.whattowatch.model.dataClasses.Provider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProviderViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    private val _event = MutableSharedFlow<Provider>()
    private val _viewState = MutableStateFlow(ProviderViewState())
    val viewState = _viewState.asStateFlow()
    private val database = FirebaseRepository()

    init {
        listenToEvent()
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val savedProviders = database.getProvider()
            val company = apiRepository.getCompanies(savedProviders)

            _viewState.update { currentState ->
                currentState.copy(providers = company.filter { provider -> provider.priority != 999 }
                    .sorted())
            }
            _viewState.update { it.copy(isLoading = false) }
        }
    }

    fun sendEvent(event: Provider) {
        viewModelScope.launch(ioDispatcher) {
            _event.emit(event)
        }
    }

    private fun listenToEvent() = viewModelScope.launch(ioDispatcher) {
        _event.collect { provider ->
            _viewState.update { state ->
                state.copy(providers = state.providers.map {
                    if (it.providerId == provider.providerId) it.copy(
                        isUpdating = true
                    ) else it
                })
            }
            if (provider.show) {
                database.removeProvider(provider.providerId)
            } else {
                database.addProvider(provider.providerId)
            }
            _viewState.update { state ->
                state.copy(providers = state.providers.map {
                    if (it.providerId == provider.providerId) it.copy(
                        show = !it.show,
                        isUpdating = false
                    ) else it
                })
            }
        }
    }
}
