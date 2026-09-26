package com.weatherapp.domain.usecase

import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.model.WeatherCondition
import com.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherUseCaseTest {
    private val repository = mockk<WeatherRepository>()
    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = testDispatchers(dispatcher)
    private val current = GetCurrentWeatherUseCase(repository, dispatchers)
    private val forecast = GetForecastUseCase(repository, dispatchers)

    @Test
    fun currentWeatherRejectsInvalidCoordinates() = runTest(dispatcher) {
        val result = current(latitude = 120.0, longitude = 10.0)
        assertTrue(result.exceptionOrNull() is DomainError.InvalidCoordinates)
        coVerify(exactly = 0) { repository.getCurrentWeather(any(), any(), any(), any()) }
    }

    @Test
    fun currentWeatherDelegatesToRepository() = runTest(dispatcher) {
        val weather = sampleWeather()
        coEvery { repository.getCurrentWeather(41.01, 28.95, sampleCity(), true) } returns Result.success(weather)
        val result = current(41.01, 28.95, sampleCity(), forceRefresh = true)
        assertEquals(weather, result.getOrNull())
    }

    @Test
    fun forecastPropagatesRepositoryFailure() = runTest(dispatcher) {
        coEvery { repository.getForecast(41.0, 29.0, null, false) } returns Result.failure(DomainError.Timeout)
        val result = forecast(41.0, 29.0)
        assertTrue(result.exceptionOrNull() is DomainError.Timeout)
    }

    private fun sampleCity() = City("İstanbul", "Türkiye", "TR", "İstanbul", 41.01, 28.95, "Europe/Istanbul", 745044L)

    private fun sampleWeather() = Weather(
        city = sampleCity(),
        temperatureCelsius = 18.0,
        feelsLikeCelsius = 17.0,
        minTemperatureCelsius = 12.0,
        maxTemperatureCelsius = 21.0,
        humidityPercent = 60,
        windSpeedKmh = 14.0,
        condition = WeatherCondition.CLEAR,
        sunriseEpochMillis = 1L,
        sunsetEpochMillis = 2L,
        uvIndex = 5.0,
        precipitationProbabilityPercent = 10,
        observedAtEpochMillis = 3L,
        isFromCache = false,
        cachedAtEpochMillis = null,
    )
}
