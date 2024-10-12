package com.movies.whattowatch.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.whattowatch.FirebaseRepository
import com.movies.whattowatch.MainViewEvent
import com.movies.whattowatch.apiRepository.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProviderViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiRepository: ApiRepository,
) : ViewModel() {
    private val _event = MutableSharedFlow<MainViewEvent>()
    private val _viewState = MutableStateFlow(ProviderViewState())
    val viewState = _viewState.asStateFlow()
    private val database = FirebaseRepository()

    init {
        _viewState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val company = apiRepository.getCompanies(database.getProvider())
            _viewState.update { currentState ->
                currentState.copy(providers = company.filter { provider -> provider.priority != 999 }
                    .sorted())
            }
            _viewState.update { it.copy(isLoading = false) }
        }
    }

    fun sendEvent(event: MainViewEvent) {
        viewModelScope.launch(ioDispatcher) {
            _event.emit(event)
        }
    }
}
