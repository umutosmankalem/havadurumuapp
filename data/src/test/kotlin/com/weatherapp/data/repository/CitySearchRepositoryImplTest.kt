package com.weatherapp.data.repository

import com.weatherapp.data.remote.api.GeocodingApi
import com.weatherapp.data.remote.dto.GeocodingResponseDto
import com.weatherapp.data.remote.dto.GeocodingResultDto
import com.weatherapp.domain.error.DomainError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class CitySearchRepositoryImplTest {
    private val api = mockk<GeocodingApi>()
    private val repository = CitySearchRepositoryImpl(api)

    @Test
    fun preservesTurkishNamesFromGeocoding() = runTest {
        coEvery { api.search("Çanakkale", 20, "tr") } returns GeocodingResponseDto(
            results = listOf(
                GeocodingResultDto(
                    id = 749748,
                    name = "Çanakkale",
                    latitude = 40.1553,
                    longitude = 26.4142,
                    country = "Türkiye",
                    countryCode = "TR",
                    admin1 = "Çanakkale",
                    timezone = "Europe/Istanbul",
                ),
            ),
        )
        val cities = repository.search("Çanakkale", "tr").getOrThrow()
        assertEquals("Çanakkale", cities.single().name)
        assertEquals("TR", cities.single().countryCode)
        coVerify { api.search("Çanakkale", 20, "tr") }
    }

    @Test
    fun emptyResultsAreSuccess() = runTest {
        coEvery { api.search(any(), any(), any()) } returns GeocodingResponseDto(results = emptyList())
        assertEquals(emptyList<Any>(), repository.search("Nowhereville", "en").getOrThrow())
    }

    @Test
    fun networkFailureBecomesDomainError() = runTest {
        coEvery { api.search(any(), any(), any()) } throws IOException("down")
        assertTrue(repository.search("Ankara", "tr").exceptionOrNull() is DomainError.NoInternet)
    }
}
