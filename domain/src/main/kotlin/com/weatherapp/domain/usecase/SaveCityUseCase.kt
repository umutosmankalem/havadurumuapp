package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CoordinateRules
import com.weatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.withContext

class SaveCityUseCase(
    private val repository: FavoriteRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(city: City): Result<Unit> = withContext(dispatchers.io) {
        if (!CoordinateRules.isValid(city.latitude, city.longitude)) {
            Result.failure(DomainError.InvalidCoordinates)
        } else {
            repository.save(city)
        }
    }
}
