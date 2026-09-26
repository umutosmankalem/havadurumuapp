package com.weatherapp.data.repository

import com.weatherapp.data.local.dao.LastPlaceDao
import com.weatherapp.data.mapper.CityEntityMapper
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.LastPlaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastPlaceRepositoryImpl @Inject constructor(
    private val dao: LastPlaceDao,
) : LastPlaceRepository {
    override suspend fun get(): City? {
        return try {
            dao.get(OpenMeteoParams.SINGLE_PLACE_ROW_ID)?.let(CityEntityMapper::toCity)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun save(city: City): Result<Unit> = databaseCall {
        dao.upsert(CityEntityMapper.toLastPlace(city))
    }
}
