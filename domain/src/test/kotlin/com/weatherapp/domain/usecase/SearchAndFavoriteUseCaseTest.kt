package com.weatherapp.domain.usecase

import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.CitySearchRepository
import com.weatherapp.domain.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchAndFavoriteUseCaseTest {
    private val searchRepository = mockk<CitySearchRepository>()
    private val favorites = mockk<FavoriteRepository>()
    private val dispatcher = StandardTestDispatcher()
    private val dispatchers = testDispatchers(dispatcher)
    private val search = SearchCityUseCase(searchRepository, dispatchers)
    private val save = SaveCityUseCase(favorites, dispatchers)
    private val delete = DeleteCityUseCase(favorites, dispatchers)
    private val saved = GetSavedCitiesUseCase(favorites)

    @Test
    fun shortQueryDoesNotCallGeocoding() = runTest(dispatcher) {
        val result = search("İ", "tr")
        assertEquals(emptyList<City>(), result.getOrNull())
        coVerify(exactly = 0) { searchRepository.search(any(), any()) }
    }

    @Test
    fun turkishQueryIsForwardedUnchanged() = runTest(dispatcher) {
        val city = City("Şanlıurfa", "Türkiye", "TR", "Şanlıurfa", 37.16, 38.79, "Europe/Istanbul", 1L)
        coEvery { searchRepository.search("Şanlıurfa", "tr") } returns Result.success(listOf(city))
        val result = search("  Şanlıurfa  ", "tr")
        assertEquals(listOf(city), result.getOrNull())
    }

    @Test
    fun searchFailureIsPreserved() = runTest(dispatcher) {
        coEvery { searchRepository.search("Paris", "en") } returns Result.failure(DomainError.NoInternet)
        assertTrue(search("Paris", "en").exceptionOrNull() is DomainError.NoInternet)
    }

    @Test
    fun saveRejectsInvalidCoordinates() = runTest(dispatcher) {
        val city = City("Nowhere", "", "", "", 400.0, 0.0, "", null)
        assertTrue(save(city).exceptionOrNull() is DomainError.InvalidCoordinates)
    }

    @Test
    fun deleteUsesStableCityKey() = runTest(dispatcher) {
        val city = City("Çorum", "Türkiye", "TR", "Çorum", 40.55, 34.95, "Europe/Istanbul", 42L)
        coEvery { favorites.delete("geo:42") } returns Result.success(Unit)
        assertTrue(delete(city).isSuccess)
        coVerify { favorites.delete("geo:42") }
    }

    @Test
    fun savedCitiesEmitsRepositoryResults() = runTest(dispatcher) {
        val city = City("İzmir", "Türkiye", "TR", "İzmir", 38.42, 27.14, "Europe/Istanbul", 7L)
        every { favorites.observeFavorites() } returns flowOf(Result.success(listOf(city)))
        saved().test {
            assertEquals(listOf(city), awaitItem().getOrNull())
            awaitComplete()
        }
    }
}
