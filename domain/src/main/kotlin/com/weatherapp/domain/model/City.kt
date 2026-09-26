package com.weatherapp.domain.model

import java.util.Locale

data class City(
    val name: String,
    val country: String,
    val countryCode: String,
    val adminArea: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val geocodingId: Long?,
)

object CityKeys {
    fun of(city: City): String {
        city.geocodingId?.let { id -> return "geo:$id" }
        return coordinate(city.latitude, city.longitude)
    }

    fun coordinate(latitude: Double, longitude: Double): String {
        return "coord:${format(latitude)},${format(longitude)}"
    }

    private fun format(value: Double): String = String.format(Locale.US, "%.4f", value)
}

object CoordinateRules {
    const val MIN_LATITUDE = -90.0
    const val MAX_LATITUDE = 90.0
    const val MIN_LONGITUDE = -180.0
    const val MAX_LONGITUDE = 180.0

    fun isValid(latitude: Double, longitude: Double): Boolean {
        return latitude in MIN_LATITUDE..MAX_LATITUDE && longitude in MIN_LONGITUDE..MAX_LONGITUDE
    }
}

object SearchRules {
    const val MIN_QUERY_LENGTH = 2
}
