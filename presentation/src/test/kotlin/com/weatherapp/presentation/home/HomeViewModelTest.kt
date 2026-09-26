package com.weatherapp.presentation.home

import app.cash.turbine.test
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.model.WeatherCondition
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetForecastUseCase
import com.weatherapp.domain.usecase.ObserveCityFavoriteUseCase
import com.weatherapp.domain.usecase.RememberCityUseCase
import com.weatherapp.domain.usecase.ResolveHomeCityUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.presentation.message.UserMessage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getCurrentWeather = mockk<GetCurrentWeatherUseCase>()
    private val getForecast = mockk<GetForecastUseCase>()
    private val resolveHomeCity = mockk<ResolveHomeCityUseCase>()
    private val rememberCity = mockk<RememberCityUseCase>()
    private val observeFavorite = mockk<ObserveCityFavoriteUseCase>()
    private val saveCity = mockk<SaveCityUseCase>()
    private val deleteCity = mockk<DeleteCityUseCase>()
    private val city = City("Ankara", "Türkiye", "TR", "Ankara", 39.93, 32.85, "Europe/Istanbul", 323786L)
    private val weather = Weather(
        city = city,
        temperatureCelsius = 11.0,
        feelsLikeCelsius = 9.0,
        minTemperatureCelsius = 4.0,
        maxTemperatureCelsius = 13.0,
        humidityPercent = 70,
        windSpeedKmh = 8.0,
        condition = WeatherCondition.CLOUDY,
        sunriseEpochMillis = 10L,
        sunsetEpochMillis = 20L,
        uvIndex = 2.0,
        precipitationProbabilityPercent = 30,
        observedAtEpochMillis = 30L,
        isFromCache = true,
        cachedAtEpochMillis = 30L,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { rememberCity(any()) } returns Result.success(Unit)
        every { observeFavorite(any()) } returns flowOf(false)
        coEvery { getForecast(any(), any(), any(), any()) } returns Result.success(Forecast(emptyList(), emptyList(), false, null))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsWeatherAfterPermissionGrant() = runTest(dispatcher) {
        coEvery { resolveHomeCity(true) } returns Result.success(city)
        coEvery { getCurrentWeather(city.latitude, city.longitude, city, false) } returns Result.success(weather)
        val viewModel = viewModel()
        viewModel.state.test {
            assertEquals(true, awaitItem().isLoading)
            viewModel.onPermissionResolved(LocationPermissionStatus.Granted)
            advanceUntilIdle()
            val loaded = expectMostRecentItem()
            assertFalse(loaded.isLoading)
            assertEquals(weather, loaded.weather)
            assertNull(loaded.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun permissionDeniedWithoutSavedCityShowsMessage() = runTest(dispatcher) {
        coEvery { resolveHomeCity(false) } returns Result.failure(DomainError.LocationPermissionDenied)
        val viewModel = viewModel()
        viewModel.state.test {
            awaitItem()
            viewModel.onPermissionResolved(LocationPermissionStatus.Denied)
            advanceUntilIdle()
            assertEquals(UserMessage.LocationPermission, expectMostRecentItem().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewModel() = HomeViewModel(
        getCurrentWeather,
        getForecast,
        resolveHomeCity,
        rememberCity,
        observeFavorite,
        saveCity,
        deleteCity,
    )
}
