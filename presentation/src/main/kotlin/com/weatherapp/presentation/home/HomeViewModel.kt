package com.weatherapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetForecastUseCase
import com.weatherapp.domain.usecase.ObserveCityFavoriteUseCase
import com.weatherapp.domain.usecase.RememberCityUseCase
import com.weatherapp.domain.usecase.ResolveHomeCityUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.message.toUserMessage
import com.weatherapp.presentation.weather.WeatherLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LocationPermissionStatus {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied,
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val weather: Weather? = null,
    val forecast: Forecast? = null,
    val error: UserMessage? = null,
    val permission: LocationPermissionStatus = LocationPermissionStatus.Unknown,
    val isFavorite: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getCurrentWeather: GetCurrentWeatherUseCase,
    getForecast: GetForecastUseCase,
    private val resolveHomeCity: ResolveHomeCityUseCase,
    private val rememberCity: RememberCityUseCase,
    private val observeCityFavorite: ObserveCityFavoriteUseCase,
    private val saveCity: SaveCityUseCase,
    private val deleteCity: DeleteCityUseCase,
) : ViewModel() {
    private val loader = WeatherLoader(getCurrentWeather, getForecast)
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    private var loadJob: Job? = null
    private var favoriteJob: Job? = null
    private var activeCity: City? = null

    fun onPermissionResolved(status: LocationPermissionStatus) {
        _state.update { it.copy(permission = status) }
        load(forceRefresh = false)
    }

    fun refresh() = load(forceRefresh = true)

    fun toggleFavorite() {
        val city = activeCity ?: return
        viewModelScope.launch {
            if (_state.value.isFavorite) deleteCity(city) else saveCity(city)
        }
    }

    private fun load(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val hasContent = _state.value.weather != null
            _state.update {
                it.copy(
                    isLoading = !hasContent,
                    isRefreshing = hasContent,
                    error = null,
                )
            }
            val useDeviceLocation = _state.value.permission == LocationPermissionStatus.Granted
            val city = resolveHomeCity(useDeviceLocation).getOrElse { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = if (it.weather == null) error.toUserMessage() else it.error,
                    )
                }
                return@launch
            }
            activeCity = city
            rememberCity(city)
            watchFavorite(city)
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

    private fun watchFavorite(city: City) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            observeCityFavorite(city).collect { favorite ->
                _state.update { it.copy(isFavorite = favorite) }
            }
        }
    }
}
