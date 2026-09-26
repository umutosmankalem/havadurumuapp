package com.weatherapp.presentation.weather

import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetForecastUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.message.toUserMessage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal data class LoadedWeather(
    val weather: Weather? = null,
    val forecast: Forecast? = null,
    val error: UserMessage? = null,
)

internal class WeatherLoader(
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val getForecast: GetForecastUseCase,
) {
    suspend fun load(city: City, forceRefresh: Boolean): LoadedWeather = coroutineScope {
        val weatherDeferred = async {
            getCurrentWeather(city.latitude, city.longitude, city, forceRefresh)
        }
        val forecastDeferred = async {
            getForecast(city.latitude, city.longitude, city, forceRefresh)
        }
        val weather = weatherDeferred.await()
        val forecast = forecastDeferred.await()
        LoadedWeather(
            weather = weather.getOrNull(),
            forecast = forecast.getOrNull(),
            error = weather.exceptionOrNull()?.toUserMessage()
                ?: forecast.exceptionOrNull()?.toUserMessage(),
        )
    }
}
