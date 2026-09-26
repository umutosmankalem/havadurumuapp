package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetSavedCitiesUseCase(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<Result<List<City>>> = repository.observeFavorites()
}
