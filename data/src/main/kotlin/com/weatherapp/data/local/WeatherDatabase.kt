package com.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatherapp.data.local.dao.FavoriteCityDao
import com.weatherapp.data.local.dao.LastPlaceDao
import com.weatherapp.data.local.dao.WeatherCacheDao
import com.weatherapp.data.local.entity.FavoriteCityEntity
import com.weatherapp.data.local.entity.LastPlaceEntity
import com.weatherapp.data.local.entity.WeatherCacheEntity

@Database(
    entities = [
        WeatherCacheEntity::class,
        FavoriteCityEntity::class,
        LastPlaceEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherCacheDao(): WeatherCacheDao

    abstract fun favoriteCityDao(): FavoriteCityDao

    abstract fun lastPlaceDao(): LastPlaceDao
}
