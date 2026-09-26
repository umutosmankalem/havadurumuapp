package com.weatherapp.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetForecastUseCase
import com.weatherapp.domain.usecase.ObserveCityFavoriteUseCase
import com.weatherapp.domain.usecase.RememberCityUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.navigation.DetailsRoute
import com.weatherapp.presentation.weather.WeatherLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherDetailsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weather: Weather? = null,
    val forecast: Forecast? = null,
    val error: UserMessage? = null,
    val isFavorite: Boolean = false,
)

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentWeather: GetCurrentWeatherUseCase,
    getForecast: GetForecastUseCase,
    private val rememberCity: RememberCityUseCase,
    private val observeCityFavorite: ObserveCityFavoriteUseCase,
    private val saveCity: SaveCityUseCase,
    private val deleteCity: DeleteCityUseCase,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DetailsRoute>()
    private val city = City(
        name = route.name,
        country = route.country,
        countryCode = route.countryCode,
        adminArea = route.adminArea,
        latitude = route.latitude,
        longitude = route.longitude,
        timezone = route.timezone,
        geocodingId = route.geocodingId.takeIf { it >= 0 },
    )
    private val loader = WeatherLoader(getCurrentWeather, getForecast)
    private val _state = MutableStateFlow(WeatherDetailsUiState())
    val state: StateFlow<WeatherDetailsUiState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch { rememberCity(city) }
        viewModelScope.launch {
            observeCityFavorite(city).collect { favorite ->
                _state.update { it.copy(isFavorite = favorite) }
            }
        }
        refresh(forceRefresh = false)
    }

    fun refresh() = refresh(forceRefresh = true)

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_state.value.isFavorite) deleteCity(city) else saveCity(city)
        }
    }

    private fun refresh(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val hasContent = _state.value.weather != null
            _state.update { it.copy(isLoading = !hasContent, isRefreshing = hasContent, error = null) }
            val loaded = loader.load(city, forceRefresh)
            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    weather = loaded.weather ?: it.weather,
                    forecast = loaded.forecast ?: it.forecast,
                    error = if (loaded.weather == null) loaded.error else null,
                )
            }
        }
    }
}
