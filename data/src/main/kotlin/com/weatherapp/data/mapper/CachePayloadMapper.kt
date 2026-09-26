package com.weatherapp.data.mapper

import com.weatherapp.data.local.entity.CachedCityPayload
import com.weatherapp.data.local.entity.CachedDayPayload
import com.weatherapp.data.local.entity.CachedHourPayload
import com.weatherapp.data.local.entity.CachedWeatherPayload
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.DailyForecast
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.HourlyForecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.model.WeatherCondition

internal data class WeatherBundle(
    val weather: Weather,
    val forecast: Forecast,
    val servedFromCache: Boolean,
    val cachedAtEpochMillis: Long?,
)

internal object CachePayloadMapper {
    fun toPayload(mapped: MappedForecast): CachedWeatherPayload {
        val weather = mapped.weather
        return CachedWeatherPayload(
            city = weather.city.toPayload(),
            temperatureCelsius = weather.temperatureCelsius,
            feelsLikeCelsius = weather.feelsLikeCelsius,
            minTemperatureCelsius = weather.minTemperatureCelsius,
            maxTemperatureCelsius = weather.maxTemperatureCelsius,
            humidityPercent = weather.humidityPercent,
            windSpeedKmh = weather.windSpeedKmh,
            condition = weather.condition.name,
            sunriseEpochMillis = weather.sunriseEpochMillis,
            sunsetEpochMillis = weather.sunsetEpochMillis,
            uvIndex = weather.uvIndex,
            precipitationProbabilityPercent = weather.precipitationProbabilityPercent,
            observedAtEpochMillis = weather.observedAtEpochMillis,
            hourly = mapped.forecast.hourly.map { hour ->
                CachedHourPayload(
                    timeEpochMillis = hour.timeEpochMillis,
                    temperatureCelsius = hour.temperatureCelsius,
                    precipitationProbabilityPercent = hour.precipitationProbabilityPercent,
                    condition = hour.condition.name,
                )
            },
            daily = mapped.forecast.daily.map { day ->
                CachedDayPayload(
                    dateEpochMillis = day.dateEpochMillis,
                    minTemperatureCelsius = day.minTemperatureCelsius,
                    maxTemperatureCelsius = day.maxTemperatureCelsius,
                    precipitationProbabilityPercent = day.precipitationProbabilityPercent,
                    condition = day.condition.name,
                    sunriseEpochMillis = day.sunriseEpochMillis,
                    sunsetEpochMillis = day.sunsetEpochMillis,
                    uvIndex = day.uvIndex,
                )
            },
        )
    }

    fun toBundle(payload: CachedWeatherPayload, cachedAtEpochMillis: Long): WeatherBundle {
        val city = payload.city.toCity()
        return WeatherBundle(
            weather = Weather(
                city = city,
                temperatureCelsius = payload.temperatureCelsius,
                feelsLikeCelsius = payload.feelsLikeCelsius,
                minTemperatureCelsius = payload.minTemperatureCelsius,
                maxTemperatureCelsius = payload.maxTemperatureCelsius,
                humidityPercent = payload.humidityPercent,
                windSpeedKmh = payload.windSpeedKmh,
                condition = payload.condition.toCondition(),
                sunriseEpochMillis = payload.sunriseEpochMillis,
                sunsetEpochMillis = payload.sunsetEpochMillis,
                uvIndex = payload.uvIndex,
                precipitationProbabilityPercent = payload.precipitationProbabilityPercent,
                observedAtEpochMillis = payload.observedAtEpochMillis,
                isFromCache = true,
                cachedAtEpochMillis = cachedAtEpochMillis,
            ),
            forecast = Forecast(
                hourly = payload.hourly.map { hour ->
                    HourlyForecast(
                        timeEpochMillis = hour.timeEpochMillis,
                        temperatureCelsius = hour.temperatureCelsius,
                        precipitationProbabilityPercent = hour.precipitationProbabilityPercent,
                        condition = hour.condition.toCondition(),
                    )
                },
                daily = payload.daily.map { day ->
                    DailyForecast(
                        dateEpochMillis = day.dateEpochMillis,
                        minTemperatureCelsius = day.minTemperatureCelsius,
                        maxTemperatureCelsius = day.maxTemperatureCelsius,
                        precipitationProbabilityPercent = day.precipitationProbabilityPercent,
                        condition = day.condition.toCondition(),
                        sunriseEpochMillis = day.sunriseEpochMillis,
                        sunsetEpochMillis = day.sunsetEpochMillis,
                        uvIndex = day.uvIndex,
                    )
                },
                isFromCache = true,
                cachedAtEpochMillis = cachedAtEpochMillis,
            ),
            servedFromCache = true,
            cachedAtEpochMillis = cachedAtEpochMillis,
        )
    }

    private fun City.toPayload(): CachedCityPayload = CachedCityPayload(
        name = name,
        country = country,
        countryCode = countryCode,
        adminArea = adminArea,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        geocodingId = geocodingId,
    )

    private fun CachedCityPayload.toCity(): City = City(
        name = name,
        country = country,
        countryCode = countryCode,
        adminArea = adminArea,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        geocodingId = geocodingId,
    )

    private fun String.toCondition(): WeatherCondition {
        return runCatching { WeatherCondition.valueOf(this) }.getOrDefault(WeatherCondition.UNKNOWN)
    }
}
