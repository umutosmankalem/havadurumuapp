package com.weatherapp.presentation.search

import app.cash.turbine.test
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.LanguageProvider
import com.weatherapp.domain.usecase.SearchCityUseCase
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.theme.Motion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val searchCity = mockk<SearchCityUseCase>()
    private val language = mockk<LanguageProvider>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { language.languageCode() } returns "tr"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun debouncedQueryReturnsCities() = runTest(dispatcher) {
        val city = City("Şanlıurfa", "Türkiye", "TR", "Şanlıurfa", 37.15, 38.79, "Europe/Istanbul", 9L)
        coEvery { searchCity("Şanlıurfa", "tr") } returns Result.success(listOf(city))
        val viewModel = SearchViewModel(searchCity, language)
        viewModel.state.test {
            assertEquals("", awaitItem().query)
            viewModel.onQueryChange("Şanlıurfa")
            advanceTimeBy(Motion.SEARCH_DEBOUNCE_MILLIS)
            advanceUntilIdle()
            val latest = expectMostRecentItem()
            assertEquals(listOf(city), latest.results)
            assertTrue(latest.submitted)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { searchCity("Şanlıurfa", "tr") }
    }

    @Test
    fun failureBecomesUserMessage() = runTest(dispatcher) {
        coEvery { searchCity("Paris", "tr") } returns Result.failure(DomainError.NoInternet)
        val viewModel = SearchViewModel(searchCity, language)
        viewModel.onQueryChange("Paris")
        advanceUntilIdle()
        assertEquals(UserMessage.NoInternet, viewModel.state.value.error)
        assertTrue(viewModel.state.value.results.isEmpty())
    }
}
