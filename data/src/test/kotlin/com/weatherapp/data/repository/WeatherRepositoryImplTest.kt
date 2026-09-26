package com.weatherapp.data.repository

import com.weatherapp.core.AppClock
import com.weatherapp.data.local.dao.WeatherCacheDao
import com.weatherapp.data.local.entity.WeatherCacheEntity
import com.weatherapp.data.remote.api.OpenMeteoApi
import com.weatherapp.data.remote.dto.CurrentWeatherDto
import com.weatherapp.data.remote.dto.DailyWeatherDto
import com.weatherapp.data.remote.dto.ForecastResponseDto
import com.weatherapp.data.remote.dto.HourlyWeatherDto
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.WeatherCondition
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.SocketTimeoutException

class WeatherRepositoryImplTest {
    private val api = mockk<OpenMeteoApi>()
    private val cacheDao = mockk<WeatherCacheDao>(relaxed = true)
    private val network = mockk<NetworkStatus>()
    private val clock = object : AppClock {
        override fun nowMillis(): Long = 1_700_000_000_000L
    }
    private val json = Json { ignoreUnknownKeys = true }
    private val city = City("İzmir", "Türkiye", "TR", "İzmir", 38.42, 27.14, "Europe/Istanbul", 311046L)

    @Test
    fun successfulResponseIsCachedAndShared() = runTest {
        val repository = repository()
        coEvery { network.isOnline() } returns true
        coEvery { api.forecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns sampleDto()
        val weather = repository.getCurrentWeather(city.latitude, city.longitude, city).getOrThrow()
        val forecast = repository.getForecast(city.latitude, city.longitude, city).getOrThrow()
        assertEquals(21.5, weather.temperatureCelsius, 0.01)
        assertEquals(WeatherCondition.CLEAR, weather.condition)
        assertFalse(weather.isFromCache)
        assertEquals(1, forecast.daily.size)
        assertEquals(4.0, weather.uvIndex)
        coVerify(exactly = 1) { api.forecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 1) { cacheDao.upsert(any()) }
    }

    @Test
    fun offlineLoadReturnsCachedWeather() = runTest {
        val repository = repository()
        coEvery { network.isOnline() } returns true
        coEvery { api.forecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns sampleDto()
        repository.getCurrentWeather(city.latitude, city.longitude, city).getOrThrow()
        val stored = slot<WeatherCacheEntity>()
        coVerify { cacheDao.upsert(capture(stored)) }
        coEvery { network.isOnline() } returns false
        coEvery { cacheDao.get(stored.captured.cityKey) } returns stored.captured
        val cached = repository.getCurrentWeather(city.latitude, city.longitude, city, forceRefresh = true).getOrThrow()
        assertTrue(cached.isFromCache)
        assertEquals(21.5, cached.temperatureCelsius, 0.01)
        assertEquals("İzmir", cached.city.name)
    }

    @Test
    fun timeoutWithoutCacheFails() = runTest {
        val repository = repository()
        coEvery { network.isOnline() } returns true
        coEvery { api.forecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws SocketTimeoutException("slow")
        coEvery { cacheDao.get(any()) } returns null
        val result = repository.getCurrentWeather(city.latitude, city.longitude, city, forceRefresh = true)
        assertTrue(result.exceptionOrNull() is DomainError.Timeout)
    }

    @Test
    fun missingCurrentBlockIsInvalid() = runTest {
        val repository = repository()
        coEvery { network.isOnline() } returns true
        coEvery { api.forecast(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns ForecastResponseDto(current = null)
        coEvery { cacheDao.get(any()) } returns null
        val result = repository.getForecast(city.latitude, city.longitude, city, forceRefresh = true)
        assertTrue(result.exceptionOrNull() is DomainError.InvalidResponse)
    }

    private fun TestScope.repository() = WeatherRepositoryImpl(
        api = api,
        cacheDao = cacheDao,
        networkStatus = network,
        clock = clock,
        json = json,
        appScope = this,
    )

    private fun sampleDto() = ForecastResponseDto(
        timezone = "Europe/Istanbul",
        utcOffsetSeconds = 10800,
        current = CurrentWeatherDto(
            time = "2024-01-01T12:00",
            temperature = 21.5,
            humidity = 55.0,
            apparentTemperature = 20.0,
            weatherCode = 0,
            windSpeed = 12.0,
        ),
        hourly = HourlyWeatherDto(
            time = listOf("2024-01-01T12:00"),
            temperature = listOf(21.5),
            precipitationProbability = listOf(15.0),
            weatherCode = listOf(0),
            uvIndex = listOf(4.0),
        ),
        daily = DailyWeatherDto(
            time = listOf("2024-01-01"),
            weatherCode = listOf(1),
            temperatureMax = listOf(24.0),
            temperatureMin = listOf(14.0),
            sunrise = listOf("2024-01-01T07:30"),
            sunset = listOf("2024-01-01T17:45"),
            precipitationProbabilityMax = listOf(20.0),
            uvIndexMax = listOf(5.0),
        ),
    )
}
