package com.weatherapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.SearchRules
import com.weatherapp.domain.repository.LanguageProvider
import com.weatherapp.domain.usecase.SearchCityUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.message.toUserMessage
import com.weatherapp.presentation.theme.Motion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<City> = emptyList(),
    val error: UserMessage? = null,
    val submitted: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCity: SearchCityUseCase,
    private val languageProvider: LanguageProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()
    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.trim().length < SearchRules.MIN_QUERY_LENGTH) {
            _state.update { it.copy(isLoading = false, results = emptyList(), error = null, submitted = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(Motion.SEARCH_DEBOUNCE_MILLIS)
            _state.update { it.copy(isLoading = true, error = null) }
            searchCity(query, languageProvider.languageCode()).fold(
                onSuccess = { cities ->
                    _state.update { it.copy(isLoading = false, results = cities, submitted = true, error = null) }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isLoading = false, results = emptyList(), submitted = true, error = error.toUserMessage())
                    }
                },
            )
        }
    }

    fun retry() = onQueryChange(_state.value.query)
}
