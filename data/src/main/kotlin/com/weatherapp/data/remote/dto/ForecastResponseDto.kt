package com.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val timezone: String? = null,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int? = null,
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null,
    val daily: DailyWeatherDto? = null,
    val error: Boolean? = null,
)

@Serializable
data class CurrentWeatherDto(
    val time: String? = null,
    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("relative_humidity_2m") val humidity: Double? = null,
    @SerialName("apparent_temperature") val apparentTemperature: Double? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed: Double? = null,
)

@Serializable
data class HourlyWeatherDto(
    val time: List<String>? = null,
    @SerialName("temperature_2m") val temperature: List<Double?>? = null,
    @SerialName("precipitation_probability") val precipitationProbability: List<Double?>? = null,
    @SerialName("weather_code") val weatherCode: List<Int?>? = null,
    @SerialName("uv_index") val uvIndex: List<Double?>? = null,
)

@Serializable
data class DailyWeatherDto(
    val time: List<String>? = null,
    @SerialName("weather_code") val weatherCode: List<Int?>? = null,
    @SerialName("temperature_2m_max") val temperatureMax: List<Double?>? = null,
    @SerialName("temperature_2m_min") val temperatureMin: List<Double?>? = null,
    val sunrise: List<String?>? = null,
    val sunset: List<String?>? = null,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Double?>? = null,
    @SerialName("uv_index_max") val uvIndexMax: List<Double?>? = null,
)
