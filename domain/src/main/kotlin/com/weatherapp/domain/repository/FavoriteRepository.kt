package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<Result<List<City>>>

    fun observeFavorite(cityKey: String): Flow<Boolean>

    suspend fun save(city: City): Result<Unit>

    suspend fun delete(cityKey: String): Result<Unit>
}
