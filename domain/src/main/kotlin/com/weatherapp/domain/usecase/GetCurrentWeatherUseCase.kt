package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CoordinateRules
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.withContext

class GetCurrentWeatherUseCase(
    private val repository: WeatherRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        city: City? = null,
        forceRefresh: Boolean = false,
    ): Result<Weather> = withContext(dispatchers.io) {
        if (!CoordinateRules.isValid(latitude, longitude)) {
            Result.failure(DomainError.InvalidCoordinates)
        } else {
            repository.getCurrentWeather(latitude, longitude, city, forceRefresh)
        }
    }
}
