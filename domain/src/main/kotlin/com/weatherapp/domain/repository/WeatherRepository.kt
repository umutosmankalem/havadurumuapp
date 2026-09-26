package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        city: City? = null,
        forceRefresh: Boolean = false,
    ): Result<Weather>

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        city: City? = null,
        forceRefresh: Boolean = false,
    ): Result<Forecast>
}
