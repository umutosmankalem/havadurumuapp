package com.weatherapp.data.mapper

import com.weatherapp.data.remote.dto.GeocodingResponseDto
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CoordinateRules

object GeocodingMapper {
    fun map(dto: GeocodingResponseDto): List<City> {
        return dto.results.orEmpty().mapNotNull { result ->
            val name = result.name?.trim().orEmpty()
            val latitude = result.latitude
            val longitude = result.longitude
            if (name.isEmpty() || latitude == null || longitude == null) return@mapNotNull null
            if (!CoordinateRules.isValid(latitude, longitude)) return@mapNotNull null
            City(
                name = name,
                country = result.country.orEmpty(),
                countryCode = result.countryCode.orEmpty(),
                adminArea = result.admin1.orEmpty(),
                latitude = latitude,
                longitude = longitude,
                timezone = result.timezone.orEmpty(),
                geocodingId = result.id,
            )
        }
    }
}
