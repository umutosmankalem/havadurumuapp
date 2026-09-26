package com.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val cityKey: String,
    val payload: String,
    val cachedAtEpochMillis: Long,
)

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val cityKey: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val adminArea: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val geocodingId: Long?,
    val savedAtEpochMillis: Long,
)

@Entity(tableName = "last_place")
data class LastPlaceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val country: String,
    val countryCode: String,
    val adminArea: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val geocodingId: Long?,
)
