package com.weatherapp.data.repository

import com.weatherapp.core.AppClock
import com.weatherapp.data.local.dao.FavoriteCityDao
import com.weatherapp.data.mapper.CityEntityMapper
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteCityDao,
    private val clock: AppClock,
) : FavoriteRepository {
    override fun observeFavorites(): Flow<Result<List<City>>> {
        return dao.observeAll()
            .map<List<com.weatherapp.data.local.entity.FavoriteCityEntity>, Result<List<City>>> { rows ->
                Result.success(rows.map(CityEntityMapper::toCity))
            }
            .catch { error ->
                if (error is CancellationException) throw error
                emit(Result.failure(DomainError.Database))
            }
    }

    override fun observeFavorite(cityKey: String): Flow<Boolean> {
        return dao.observeExists(cityKey).catch { emit(false) }
    }

    override suspend fun save(city: City): Result<Unit> = databaseCall {
        dao.upsert(CityEntityMapper.toFavorite(city, clock.nowMillis()))
    }

    override suspend fun delete(cityKey: String): Result<Unit> = databaseCall {
        dao.delete(cityKey)
    }
}

internal suspend fun <T> databaseCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        Result.failure(DomainError.Database)
    }
}
