package com.weatherapp.domain.model

data class HourlyForecast(
    val timeEpochMillis: Long,
    val temperatureCelsius: Double,
    val precipitationProbabilityPercent: Int?,
    val condition: WeatherCondition,
)

data class DailyForecast(
    val dateEpochMillis: Long,
    val minTemperatureCelsius: Double,
    val maxTemperatureCelsius: Double,
    val precipitationProbabilityPercent: Int?,
    val condition: WeatherCondition,
    val sunriseEpochMillis: Long?,
    val sunsetEpochMillis: Long?,
    val uvIndex: Double?,
)

data class Forecast(
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val isFromCache: Boolean,
    val cachedAtEpochMillis: Long?,
)
