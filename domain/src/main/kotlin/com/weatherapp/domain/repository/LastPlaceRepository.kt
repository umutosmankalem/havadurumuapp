package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City

interface LastPlaceRepository {
    suspend fun get(): City?

    suspend fun save(city: City): Result<Unit>
}
