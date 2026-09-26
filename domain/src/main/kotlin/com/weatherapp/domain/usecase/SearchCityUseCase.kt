package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.SearchRules
import com.weatherapp.domain.repository.CitySearchRepository
import kotlinx.coroutines.withContext

class SearchCityUseCase(
    private val repository: CitySearchRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(query: String, languageCode: String): Result<List<City>> =
        withContext(dispatchers.io) {
            val trimmed = query.trim()
            if (trimmed.length < SearchRules.MIN_QUERY_LENGTH) {
                Result.success(emptyList())
            } else {
                repository.search(trimmed, languageCode)
            }
        }
}
