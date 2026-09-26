package com.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.weatherapp.data.local.entity.FavoriteCityEntity
import com.weatherapp.data.local.entity.LastPlaceEntity
import com.weatherapp.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE cityKey = :cityKey LIMIT 1")
    suspend fun get(cityKey: String): WeatherCacheEntity?

    @Upsert
    suspend fun upsert(entity: WeatherCacheEntity)
}

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY savedAtEpochMillis DESC")
    fun observeAll(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE cityKey = :cityKey)")
    fun observeExists(cityKey: String): Flow<Boolean>

    @Upsert
    suspend fun upsert(entity: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE cityKey = :cityKey")
    suspend fun delete(cityKey: String)
}

@Dao
interface LastPlaceDao {
    @Query("SELECT * FROM last_place WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): LastPlaceEntity?

    @Upsert
    suspend fun upsert(entity: LastPlaceEntity)
}
