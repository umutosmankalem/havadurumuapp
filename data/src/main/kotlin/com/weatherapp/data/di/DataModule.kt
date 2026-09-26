package com.weatherapp.data.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.weatherapp.core.AppClock
import com.weatherapp.core.DefaultDispatcherProvider
import com.weatherapp.core.DispatcherProvider
import com.weatherapp.core.SystemAppClock
import com.weatherapp.data.BuildConfig
import com.weatherapp.data.local.WeatherDatabase
import com.weatherapp.data.local.dao.FavoriteCityDao
import com.weatherapp.data.local.dao.LastPlaceDao
import com.weatherapp.data.local.dao.WeatherCacheDao
import com.weatherapp.data.remote.ApiEndpoints
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.data.remote.api.GeocodingApi
import com.weatherapp.data.remote.api.OpenMeteoApi
import com.weatherapp.data.repository.CitySearchRepositoryImpl
import com.weatherapp.data.repository.FavoriteRepositoryImpl
import com.weatherapp.data.repository.LastPlaceRepositoryImpl
import com.weatherapp.data.repository.NetworkStatus
import com.weatherapp.data.repository.ConnectivityNetworkStatus
import com.weatherapp.data.repository.SettingsRepositoryImpl
import com.weatherapp.data.repository.WeatherRepositoryImpl
import com.weatherapp.data.locale.AndroidLanguageProvider
import com.weatherapp.data.location.FusedUserLocationRepository
import com.weatherapp.domain.repository.CitySearchRepository
import com.weatherapp.domain.repository.FavoriteRepository
import com.weatherapp.domain.repository.LanguageProvider
import com.weatherapp.domain.repository.LastPlaceRepository
import com.weatherapp.domain.repository.SettingsRepository
import com.weatherapp.domain.repository.UserLocationRepository
import com.weatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForecastClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(OpenMeteoParams.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(OpenMeteoParams.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @ForecastClient
    fun provideForecastRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return retrofit(client, json, ApiEndpoints.FORECAST_BASE_URL)
    }

    @Provides
    @Singleton
    @GeocodingClient
    fun provideGeocodingRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return retrofit(client, json, ApiEndpoints.GEOCODING_BASE_URL)
    }

    @Provides
    @Singleton
    fun provideOpenMeteoApi(@ForecastClient retrofit: Retrofit): OpenMeteoApi {
        return retrofit.create(OpenMeteoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(@GeocodingClient retrofit: Retrofit): GeocodingApi {
        return retrofit.create(GeocodingApi::class.java)
    }

    private fun retrofit(client: OkHttpClient, json: Json, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
    }

    private const val JSON_MEDIA_TYPE = "application/json"
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(context, WeatherDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    fun provideWeatherCacheDao(database: WeatherDatabase): WeatherCacheDao = database.weatherCacheDao()

    @Provides
    fun provideFavoriteCityDao(database: WeatherDatabase): FavoriteCityDao = database.favoriteCityDao()

    @Provides
    fun provideLastPlaceDao(database: WeatherDatabase): LastPlaceDao = database.lastPlaceDao()

    private const val DATABASE_NAME = "nimbus.db"
}

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {
    @Provides
    @Singleton
    fun provideDispatchers(): DispatcherProvider = DefaultDispatcherProvider

    @Provides
    @Singleton
    fun provideClock(): AppClock = SystemAppClock

    @Provides
    @Singleton
    fun provideApplicationScope(dispatchers: DispatcherProvider): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatchers.io)
    }

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCitySearchRepository(impl: CitySearchRepositoryImpl): CitySearchRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindLastPlaceRepository(impl: LastPlaceRepositoryImpl): LastPlaceRepository

    @Binds
    @Singleton
    abstract fun bindUserLocationRepository(impl: FusedUserLocationRepository): UserLocationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindNetworkStatus(impl: ConnectivityNetworkStatus): NetworkStatus

    @Binds
    @Singleton
    abstract fun bindLanguageProvider(impl: AndroidLanguageProvider): LanguageProvider
}
