package com.weatherapp.domain.model

data class Weather(
    val city: City,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val minTemperatureCelsius: Double?,
    val maxTemperatureCelsius: Double?,
    val humidityPercent: Int?,
    val windSpeedKmh: Double?,
    val condition: WeatherCondition,
    val sunriseEpochMillis: Long?,
    val sunsetEpochMillis: Long?,
    val uvIndex: Double?,
    val precipitationProbabilityPercent: Int?,
    val observedAtEpochMillis: Long,
    val isFromCache: Boolean,
    val cachedAtEpochMillis: Long?,
)
