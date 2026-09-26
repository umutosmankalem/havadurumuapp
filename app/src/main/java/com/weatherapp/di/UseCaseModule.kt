package com.weatherapp.di

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.repository.CitySearchRepository
import com.weatherapp.domain.repository.FavoriteRepository
import com.weatherapp.domain.repository.LastPlaceRepository
import com.weatherapp.domain.repository.SettingsRepository
import com.weatherapp.domain.repository.UserLocationRepository
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetForecastUseCase
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.domain.usecase.ObserveCityFavoriteUseCase
import com.weatherapp.domain.usecase.ObserveThemeModeUseCase
import com.weatherapp.domain.usecase.RememberCityUseCase
import com.weatherapp.domain.usecase.ResolveHomeCityUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.domain.usecase.SearchCityUseCase
import com.weatherapp.domain.usecase.SetThemeModeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun getCurrentWeather(repository: WeatherRepository, dispatchers: DispatcherProvider) =
        GetCurrentWeatherUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun getForecast(repository: WeatherRepository, dispatchers: DispatcherProvider) =
        GetForecastUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun searchCity(repository: CitySearchRepository, dispatchers: DispatcherProvider) =
        SearchCityUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun getSavedCities(repository: FavoriteRepository) = GetSavedCitiesUseCase(repository)

    @Provides
    @Singleton
    fun saveCity(repository: FavoriteRepository, dispatchers: DispatcherProvider) =
        SaveCityUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun deleteCity(repository: FavoriteRepository, dispatchers: DispatcherProvider) =
        DeleteCityUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun observeFavorite(repository: FavoriteRepository) = ObserveCityFavoriteUseCase(repository)

    @Provides
    @Singleton
    fun resolveHomeCity(
        locationRepository: UserLocationRepository,
        lastPlaceRepository: LastPlaceRepository,
        dispatchers: DispatcherProvider,
    ) = ResolveHomeCityUseCase(locationRepository, lastPlaceRepository, dispatchers)

    @Provides
    @Singleton
    fun rememberCity(repository: LastPlaceRepository, dispatchers: DispatcherProvider) =
        RememberCityUseCase(repository, dispatchers)

    @Provides
    @Singleton
    fun observeTheme(repository: SettingsRepository) = ObserveThemeModeUseCase(repository)

    @Provides
    @Singleton
    fun setTheme(repository: SettingsRepository, dispatchers: DispatcherProvider) =
        SetThemeModeUseCase(repository, dispatchers)
}
