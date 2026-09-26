package com.weatherapp.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class CachedWeatherPayload(
    val city: CachedCityPayload,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val minTemperatureCelsius: Double? = null,
    val maxTemperatureCelsius: Double? = null,
    val humidityPercent: Int? = null,
    val windSpeedKmh: Double? = null,
    val condition: String,
    val sunriseEpochMillis: Long? = null,
    val sunsetEpochMillis: Long? = null,
    val uvIndex: Double? = null,
    val precipitationProbabilityPercent: Int? = null,
    val observedAtEpochMillis: Long,
    val hourly: List<CachedHourPayload> = emptyList(),
    val daily: List<CachedDayPayload> = emptyList(),
)

@Serializable
data class CachedCityPayload(
    val name: String,
    val country: String,
    val countryCode: String,
    val adminArea: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val geocodingId: Long? = null,
)

@Serializable
data class CachedHourPayload(
    val timeEpochMillis: Long,
    val temperatureCelsius: Double,
    val precipitationProbabilityPercent: Int? = null,
    val condition: String,
)

@Serializable
data class CachedDayPayload(
    val dateEpochMillis: Long,
    val minTemperatureCelsius: Double,
    val maxTemperatureCelsius: Double,
    val precipitationProbabilityPercent: Int? = null,
    val condition: String,
    val sunriseEpochMillis: Long? = null,
    val sunsetEpochMillis: Long? = null,
    val uvIndex: Double? = null,
)
