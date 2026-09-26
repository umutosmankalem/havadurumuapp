package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveThemeModeUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = repository.observeThemeMode()
}
