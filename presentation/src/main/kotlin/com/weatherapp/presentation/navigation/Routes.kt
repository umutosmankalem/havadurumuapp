package com.weatherapp.presentation.navigation

import com.weatherapp.domain.model.City
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data object FavoritesRoute

@Serializable
data object SettingsRoute

@Serializable
data class DetailsRoute(
    val name: String,
    val country: String,
    val countryCode: String,
    val adminArea: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val geocodingId: Long = NO_GEOCODING_ID,
) {
    companion object {
        const val NO_GEOCODING_ID = -1L
    }
}

internal fun City.toDetailsRoute(): DetailsRoute = DetailsRoute(
    name = name,
    country = country,
    countryCode = countryCode,
    adminArea = adminArea,
    latitude = latitude,
    longitude = longitude,
    timezone = timezone,
    geocodingId = geocodingId ?: DetailsRoute.NO_GEOCODING_ID,
)
