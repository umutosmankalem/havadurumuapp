package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City

interface UserLocationRepository {
    suspend fun currentCity(): Result<City>
}
