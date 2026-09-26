package com.weatherapp.data.mapper

import com.weatherapp.data.local.entity.FavoriteCityEntity
import com.weatherapp.data.local.entity.LastPlaceEntity
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityKeys

object CityEntityMapper {
    fun toFavorite(city: City, savedAtEpochMillis: Long): FavoriteCityEntity = FavoriteCityEntity(
        cityKey = CityKeys.of(city),
        name = city.name,
        country = city.country,
        countryCode = city.countryCode,
        adminArea = city.adminArea,
        latitude = city.latitude,
        longitude = city.longitude,
        timezone = city.timezone,
        geocodingId = city.geocodingId,
        savedAtEpochMillis = savedAtEpochMillis,
    )

    fun toCity(entity: FavoriteCityEntity): City = City(
        name = entity.name,
        country = entity.country,
        countryCode = entity.countryCode,
        adminArea = entity.adminArea,
        latitude = entity.latitude,
        longitude = entity.longitude,
        timezone = entity.timezone,
        geocodingId = entity.geocodingId,
    )

    fun toLastPlace(city: City): LastPlaceEntity = LastPlaceEntity(
        id = OpenMeteoParams.SINGLE_PLACE_ROW_ID,
        name = city.name,
        country = city.country,
        countryCode = city.countryCode,
        adminArea = city.adminArea,
        latitude = city.latitude,
        longitude = city.longitude,
        timezone = city.timezone,
        geocodingId = city.geocodingId,
    )

    fun toCity(entity: LastPlaceEntity): City = City(
        name = entity.name,
        country = entity.country,
        countryCode = entity.countryCode,
        adminArea = entity.adminArea,
        latitude = entity.latitude,
        longitude = entity.longitude,
        timezone = entity.timezone,
        geocodingId = entity.geocodingId,
    )
}
