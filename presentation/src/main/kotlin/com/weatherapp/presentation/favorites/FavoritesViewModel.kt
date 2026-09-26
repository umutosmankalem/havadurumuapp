package com.weatherapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.message.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val cities: List<City> = emptyList(),
    val error: UserMessage? = null,
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getSavedCities: GetSavedCitiesUseCase,
    private val deleteCity: DeleteCityUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getSavedCities().collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        _state.value = FavoritesUiState(isLoading = false, cities = cities)
                    },
                    onFailure = { error ->
                        _state.value = FavoritesUiState(isLoading = false, error = error.toUserMessage())
                    },
                )
            }
        }
    }

    fun delete(city: City) {
        viewModelScope.launch {
            deleteCity(city).onFailure { error ->
                _state.update { it.copy(error = error.toUserMessage()) }
            }
        }
    }
}
