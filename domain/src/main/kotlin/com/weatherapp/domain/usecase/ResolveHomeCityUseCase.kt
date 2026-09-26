package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.LastPlaceRepository
import com.weatherapp.domain.repository.UserLocationRepository
import kotlinx.coroutines.withContext

class ResolveHomeCityUseCase(
    private val locationRepository: UserLocationRepository,
    private val lastPlaceRepository: LastPlaceRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(useDeviceLocation: Boolean): Result<City> = withContext(dispatchers.io) {
        if (!useDeviceLocation) {
            return@withContext lastPlaceRepository.get()?.let { Result.success(it) }
                ?: Result.failure(DomainError.LocationPermissionDenied)
        }
        val located = locationRepository.currentCity()
        if (located.isSuccess) {
            lastPlaceRepository.save(located.getOrThrow())
            return@withContext located
        }
        val fallback = lastPlaceRepository.get()
        if (fallback != null) Result.success(fallback) else located
    }
}
