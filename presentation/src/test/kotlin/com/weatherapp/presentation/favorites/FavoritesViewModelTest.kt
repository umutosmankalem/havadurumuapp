package com.weatherapp.presentation.favorites

import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
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
import org.junit.Before
import org.junit.Test

class FavoritesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getSavedCities = mockk<GetSavedCitiesUseCase>()
    private val deleteCity = mockk<DeleteCityUseCase>()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun showsSavedCities() = runTest(dispatcher) {
        val city = City("Trabzon", "Türkiye", "TR", "Trabzon", 41.0, 39.7, "Europe/Istanbul", 3L)
        every { getSavedCities() } returns flowOf(Result.success(listOf(city)))
        val viewModel = FavoritesViewModel(getSavedCities, deleteCity)
        advanceUntilIdle()
        assertEquals(listOf(city), viewModel.state.value.cities)
    }

    @Test
    fun databaseFailureIsMapped() = runTest(dispatcher) {
        every { getSavedCities() } returns flowOf(Result.failure(DomainError.Database))
        val viewModel = FavoritesViewModel(getSavedCities, deleteCity)
        advanceUntilIdle()
        assertEquals(UserMessage.Database, viewModel.state.value.error)
    }

    @Test
    fun deleteFailureSurfacesMessage() = runTest(dispatcher) {
        val city = City("Rize", "Türkiye", "TR", "Rize", 41.02, 40.52, "Europe/Istanbul", 4L)
        every { getSavedCities() } returns flowOf(Result.success(listOf(city)))
        coEvery { deleteCity(city) } returns Result.failure(DomainError.Database)
        val viewModel = FavoritesViewModel(getSavedCities, deleteCity)
        advanceUntilIdle()
        viewModel.delete(city)
        advanceUntilIdle()
        assertEquals(UserMessage.Database, viewModel.state.value.error)
    }
}
