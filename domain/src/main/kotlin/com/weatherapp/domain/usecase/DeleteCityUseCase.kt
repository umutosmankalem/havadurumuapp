package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityKeys
import com.weatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.withContext

class DeleteCityUseCase(
    private val repository: FavoriteRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(city: City): Result<Unit> = withContext(dispatchers.io) {
        repository.delete(CityKeys.of(city))
    }
}
