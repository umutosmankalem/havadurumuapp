package com.weatherapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.weatherapp.domain.model.ThemeMode

private val LightScheme = lightColorScheme(
    primary = NimbusColors.Sky,
    onPrimary = NimbusColors.OnSky,
    secondary = NimbusColors.Amber,
    onSecondary = NimbusColors.OnSky,
    background = NimbusColors.Paper,
    onBackground = NimbusColors.Ink,
    surface = NimbusColors.Card,
    onSurface = NimbusColors.Ink,
    surfaceContainer = NimbusColors.CardContainer,
    surfaceVariant = NimbusColors.CardContainer,
    onSurfaceVariant = NimbusColors.InkSoft,
    error = NimbusColors.Danger,
    onError = NimbusColors.OnSky,
)

private val DarkScheme = darkColorScheme(
    primary = NimbusColors.SkyDark,
    onPrimary = NimbusColors.OnSkyDark,
    secondary = NimbusColors.AmberSoft,
    onSecondary = NimbusColors.Ink,
    background = NimbusColors.Night,
    onBackground = NimbusColors.Mist,
    surface = NimbusColors.NightCard,
    onSurface = NimbusColors.Mist,
    surfaceContainer = NimbusColors.NightContainer,
    surfaceVariant = NimbusColors.NightContainer,
    onSurfaceVariant = NimbusColors.Mist,
    error = NimbusColors.DangerDark,
    onError = NimbusColors.Ink,
)

@Composable
fun NimbusTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = NimbusTypography,
        content = content,
    )
}
