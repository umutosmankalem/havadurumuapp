package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityKeys
import com.weatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveCityFavoriteUseCase(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(city: City): Flow<Boolean> = repository.observeFavorite(CityKeys.of(city))
}
