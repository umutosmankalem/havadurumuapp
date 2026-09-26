package com.weatherapp.data.repository

import com.weatherapp.data.mapper.GeocodingMapper
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.data.remote.api.GeocodingApi
import com.weatherapp.data.remote.remoteCall
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.CitySearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitySearchRepositoryImpl @Inject constructor(
    private val api: GeocodingApi,
) : CitySearchRepository {
    override suspend fun search(query: String, languageCode: String): Result<List<City>> {
        return remoteCall {
            api.search(
                name = query,
                count = OpenMeteoParams.GEOCODING_RESULT_COUNT,
                language = languageCode.ifBlank { DEFAULT_LANGUAGE },
            )
        }.map(GeocodingMapper::map)
    }

    private companion object {
        const val DEFAULT_LANGUAGE = "en"
    }
}
