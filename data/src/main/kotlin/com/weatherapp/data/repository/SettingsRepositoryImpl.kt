package com.weatherapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "nimbus_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {
    override fun observeThemeMode(): Flow<ThemeMode> {
        return context.settingsDataStore.data.map { preferences ->
            val stored = preferences[THEME_KEY]
            runCatching { ThemeMode.valueOf(stored ?: ThemeMode.SYSTEM.name) }
                .getOrDefault(ThemeMode.SYSTEM)
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme_mode")
    }
}
