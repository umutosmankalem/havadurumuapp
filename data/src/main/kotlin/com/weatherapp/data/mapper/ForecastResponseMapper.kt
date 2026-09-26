package com.weatherapp.data.mapper

import com.weatherapp.data.remote.dto.DailyWeatherDto
import com.weatherapp.data.remote.dto.ForecastResponseDto
import com.weatherapp.data.remote.dto.HourlyWeatherDto
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.DailyForecast
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.HourlyForecast
import com.weatherapp.domain.model.Weather

internal data class MappedForecast(
    val weather: Weather,
    val forecast: Forecast,
)

internal object ForecastResponseMapper {
    fun map(dto: ForecastResponseDto, city: City): MappedForecast? {
        val current = dto.current ?: return null
        val temperature = current.temperature ?: return null
        val offset = dto.utcOffsetSeconds ?: 0
        val hourly = mapHourly(dto.hourly, offset)
        val daily = mapDaily(dto.daily, offset)
        val today = daily.firstOrNull()
        val currentHour = hourly.firstOrNull()
        val timezone = dto.timezone?.takeIf { it.isNotBlank() } ?: city.timezone
        val resolvedCity = city.copy(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = timezone,
        )
        val weather = Weather(
            city = resolvedCity,
            temperatureCelsius = temperature,
            feelsLikeCelsius = current.apparentTemperature ?: temperature,
            minTemperatureCelsius = today?.minTemperatureCelsius,
            maxTemperatureCelsius = today?.maxTemperatureCelsius,
            humidityPercent = current.humidity?.toInt(),
            windSpeedKmh = current.windSpeed,
            condition = WeatherCodeMapper.map(current.weatherCode),
            sunriseEpochMillis = today?.sunriseEpochMillis,
            sunsetEpochMillis = today?.sunsetEpochMillis,
            uvIndex = currentHour?.let { hourUv(dto.hourly, 0) } ?: today?.uvIndex,
            precipitationProbabilityPercent = currentHour?.precipitationProbabilityPercent
                ?: today?.precipitationProbabilityPercent,
            observedAtEpochMillis = OpenMeteoTime.parse(current.time, offset) ?: 0L,
            isFromCache = false,
            cachedAtEpochMillis = null,
        )
        return MappedForecast(
            weather = weather,
            forecast = Forecast(
                hourly = hourly,
                daily = daily,
                isFromCache = false,
                cachedAtEpochMillis = null,
            ),
        )
    }

    private fun hourUv(hourly: HourlyWeatherDto?, index: Int): Double? {
        return hourly?.uvIndex?.getOrNull(index)
    }

    private fun mapHourly(hourly: HourlyWeatherDto?, offset: Int): List<HourlyForecast> {
        if (hourly == null) return emptyList()
        val times = hourly.time.orEmpty()
        return times.mapIndexedNotNull { index, time ->
            val epoch = OpenMeteoTime.parse(time, offset) ?: return@mapIndexedNotNull null
            val temperature = hourly.temperature?.getOrNull(index) ?: return@mapIndexedNotNull null
            HourlyForecast(
                timeEpochMillis = epoch,
                temperatureCelsius = temperature,
                precipitationProbabilityPercent = hourly.precipitationProbability?.getOrNull(index)?.toInt(),
                condition = WeatherCodeMapper.map(hourly.weatherCode?.getOrNull(index)),
            )
        }
    }

    private fun mapDaily(daily: DailyWeatherDto?, offset: Int): List<DailyForecast> {
        if (daily == null) return emptyList()
        val times = daily.time.orEmpty()
        return times.mapIndexedNotNull { index, time ->
            val epoch = OpenMeteoTime.parse(time, offset) ?: return@mapIndexedNotNull null
            val min = daily.temperatureMin?.getOrNull(index) ?: return@mapIndexedNotNull null
            val max = daily.temperatureMax?.getOrNull(index) ?: return@mapIndexedNotNull null
            DailyForecast(
                dateEpochMillis = epoch,
                minTemperatureCelsius = min,
                maxTemperatureCelsius = max,
                precipitationProbabilityPercent = daily.precipitationProbabilityMax?.getOrNull(index)?.toInt(),
                condition = WeatherCodeMapper.map(daily.weatherCode?.getOrNull(index)),
                sunriseEpochMillis = OpenMeteoTime.parse(daily.sunrise?.getOrNull(index), offset),
                sunsetEpochMillis = OpenMeteoTime.parse(daily.sunset?.getOrNull(index), offset),
                uvIndex = daily.uvIndexMax?.getOrNull(index),
            )
        }
    }
}
