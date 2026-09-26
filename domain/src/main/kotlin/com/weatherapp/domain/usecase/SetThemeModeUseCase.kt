package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.repository.SettingsRepository
import kotlinx.coroutines.withContext

class SetThemeModeUseCase(
    private val repository: SettingsRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(mode: ThemeMode) = withContext(dispatchers.io) {
        repository.setThemeMode(mode)
    }
}
