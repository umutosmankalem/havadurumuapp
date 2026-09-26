package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.LastPlaceRepository
import kotlinx.coroutines.withContext

class RememberCityUseCase(
    private val lastPlaceRepository: LastPlaceRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(city: City): Result<Unit> = withContext(dispatchers.io) {
        lastPlaceRepository.save(city)
    }
}
