package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City

interface CitySearchRepository {
    suspend fun search(query: String, languageCode: String): Result<List<City>>
}
