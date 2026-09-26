package com.weatherapp.data.repository

import com.weatherapp.core.AppClock
import com.weatherapp.data.local.dao.WeatherCacheDao
import com.weatherapp.data.local.entity.WeatherCacheEntity
import com.weatherapp.data.local.entity.CachedWeatherPayload
import com.weatherapp.data.mapper.CachePayloadMapper
import com.weatherapp.data.mapper.ForecastResponseMapper
import com.weatherapp.data.mapper.WeatherBundle
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.data.remote.api.OpenMeteoApi
import com.weatherapp.data.remote.remoteCall
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityKeys
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenMeteoApi,
    private val cacheDao: WeatherCacheDao,
    private val networkStatus: NetworkStatus,
    private val clock: AppClock,
    private val json: Json,
    private val appScope: CoroutineScope,
) : WeatherRepository {
    private val mutex = Mutex()
    private val memory = mutableMapOf<String, MemoryHit>()
    private val inflight = mutableMapOf<String, Deferred<Result<WeatherBundle>>>()

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        city: City?,
        forceRefresh: Boolean,
    ): Result<Weather> {
        return load(latitude, longitude, city, forceRefresh).map { bundle ->
            bundle.weather.copy(
                isFromCache = bundle.servedFromCache,
                cachedAtEpochMillis = bundle.cachedAtEpochMillis,
            )
        }
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        city: City?,
        forceRefresh: Boolean,
    ): Result<Forecast> {
        return load(latitude, longitude, city, forceRefresh).map { bundle ->
            bundle.forecast.copy(
                isFromCache = bundle.servedFromCache,
                cachedAtEpochMillis = bundle.cachedAtEpochMillis,
            )
        }
    }

    private suspend fun load(
        latitude: Double,
        longitude: Double,
        city: City?,
        forceRefresh: Boolean,
    ): Result<WeatherBundle> {
        val key = city?.let(CityKeys::of) ?: CityKeys.coordinate(latitude, longitude)
        val deferred = mutex.withLock {
            if (!forceRefresh) {
                freshLocked(key)?.let { return@withLock CompletableDeferred(Result.success(it)) }
                inflight[key]?.let { return@withLock it }
            }
            val created = appScope.async { fetch(key, latitude, longitude, city) }
            inflight[key] = created
            created.invokeOnCompletion {
                appScope.launch {
                    mutex.withLock {
                        if (inflight[key] === created) inflight.remove(key)
                    }
                }
            }
            created
        }
        return deferred.await()
    }

    private suspend fun fetch(
        key: String,
        latitude: Double,
        longitude: Double,
        city: City?,
    ): Result<WeatherBundle> {
        val resolved = city ?: City(
            name = "",
            country = "",
            countryCode = "",
            adminArea = "",
            latitude = latitude,
            longitude = longitude,
            timezone = "",
            geocodingId = null,
        )
        if (!networkStatus.isOnline()) {
            return readCache(key, DomainError.NoInternet)
        }
        val remote = remoteCall {
            api.forecast(
                latitude = latitude,
                longitude = longitude,
                current = OpenMeteoParams.CURRENT,
                hourly = OpenMeteoParams.HOURLY,
                daily = OpenMeteoParams.DAILY,
                timezone = OpenMeteoParams.TIMEZONE_AUTO,
                forecastDays = OpenMeteoParams.FORECAST_DAYS,
                forecastHours = OpenMeteoParams.HOURLY_STEPS,
                temperatureUnit = OpenMeteoParams.TEMPERATURE_UNIT_CELSIUS,
                windSpeedUnit = OpenMeteoParams.WIND_SPEED_UNIT_KMH,
            )
        }
        val dto = remote.getOrElse { error ->
            val domainError = error as? DomainError ?: DomainError.Unknown
            return readCache(key, domainError)
        }
        if (dto.error == true) {
            return readCache(key, DomainError.InvalidResponse)
        }
        val mapped = ForecastResponseMapper.map(dto, resolved)
            ?: return readCache(key, DomainError.InvalidResponse)
        val now = clock.nowMillis()
        val payload = CachePayloadMapper.toPayload(mapped)
        try {
            cacheDao.upsert(
                WeatherCacheEntity(
                    cityKey = key,
                    payload = json.encodeToString(payload),
                    cachedAtEpochMillis = now,
                ),
            )
        } catch (_: Exception) {
            // A failed cache write still leaves the live response usable.
        }
        val bundle = WeatherBundle(
            weather = mapped.weather,
            forecast = mapped.forecast,
            servedFromCache = false,
            cachedAtEpochMillis = now,
        )
        mutex.withLock { memory[key] = MemoryHit(bundle, now) }
        return Result.success(bundle)
    }

    private suspend fun readCache(key: String, fallback: DomainError): Result<WeatherBundle> {
        val entity = try {
            cacheDao.get(key)
        } catch (_: Exception) {
            return Result.failure(DomainError.Database)
        }
        if (entity == null) return Result.failure(fallback)
        val payload = try {
            json.decodeFromString<CachedWeatherPayload>(entity.payload)
        } catch (_: Exception) {
            return Result.failure(DomainError.InvalidResponse)
        }
        return Result.success(CachePayloadMapper.toBundle(payload, entity.cachedAtEpochMillis))
    }

    private fun freshLocked(key: String): WeatherBundle? {
        val hit = memory[key] ?: return null
        return if (clock.nowMillis() - hit.storedAtMillis > OpenMeteoParams.MEMORY_TTL_MILLIS) {
            memory.remove(key)
            null
        } else {
            hit.bundle
        }
    }

    private data class MemoryHit(val bundle: WeatherBundle, val storedAtMillis: Long)
}
