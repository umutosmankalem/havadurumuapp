package com.weatherapp.presentation.theme

import androidx.compose.ui.graphics.Color
import com.weatherapp.domain.model.WeatherCondition

internal data class Atmosphere(
    val top: Color,
    val bottom: Color,
    val accent: Color,
    val particle: Color,
    val glow: Color,
)

internal object WeatherAtmospheres {
    fun forCondition(condition: WeatherCondition, dark: Boolean): Atmosphere = when (condition) {
        WeatherCondition.CLEAR -> if (dark) clearDark else clearLight
        WeatherCondition.PARTLY_CLOUDY -> if (dark) partlyDark else partlyLight
        WeatherCondition.CLOUDY -> if (dark) cloudyDark else cloudyLight
        WeatherCondition.RAIN -> if (dark) rainDark else rainLight
        WeatherCondition.HEAVY_RAIN -> if (dark) heavyDark else heavyLight
        WeatherCondition.THUNDERSTORM -> storm
        WeatherCondition.SNOW -> if (dark) snowDark else snowLight
        WeatherCondition.FOG -> if (dark) fogDark else fogLight
        WeatherCondition.UNKNOWN -> if (dark) cloudyDark else cloudyLight
    }

    private val clearLight = Atmosphere(
        top = Color(0xFF7EC8F2),
        bottom = Color(0xFFE7F6FF),
        accent = Color(0xFFFFC857),
        particle = Color(0xFFFFF4D2),
        glow = Color(0x66FFE08A),
    )
    private val clearDark = Atmosphere(
        top = Color(0xFF10284A),
        bottom = Color(0xFF07111E),
        accent = Color(0xFFFFD27A),
        particle = Color(0xFFFFE7A3),
        glow = Color(0x55FFD27A),
    )
    private val partlyLight = Atmosphere(
        top = Color(0xFF8FB7E8),
        bottom = Color(0xFFD5E6F8),
        accent = Color(0xFFFFD27A),
        particle = Color(0xEEFFFFFF),
        glow = Color(0x44FFE08A),
    )
    private val partlyDark = Atmosphere(
        top = Color(0xFF1A3354),
        bottom = Color(0xFF0C1726),
        accent = Color(0xFFE7C27A),
        particle = Color(0xCCE8EEF8),
        glow = Color(0x33FFE08A),
    )
    private val cloudyLight = Atmosphere(
        top = Color(0xFF9AA8B8),
        bottom = Color(0xFFD5DDE6),
        accent = Color(0xFFE7EEF6),
        particle = Color(0xF2FFFFFF),
        glow = Color(0x22FFFFFF),
    )
    private val cloudyDark = Atmosphere(
        top = Color(0xFF243140),
        bottom = Color(0xFF101820),
        accent = Color(0xFFC5D0DC),
        particle = Color(0xD9D5DEE8),
        glow = Color(0x22FFFFFF),
    )
    private val rainLight = Atmosphere(
        top = Color(0xFF60758C),
        bottom = Color(0xFFC5D0DC),
        accent = Color(0xFFD6E4F0),
        particle = Color(0xCCE8F4FF),
        glow = Color(0x22000000),
    )
    private val rainDark = Atmosphere(
        top = Color(0xFF1B2836),
        bottom = Color(0xFF0A1016),
        accent = Color(0xFF9BB4C9),
        particle = Color(0xB3D7E8F8),
        glow = Color(0x22000000),
    )
    private val heavyLight = Atmosphere(
        top = Color(0xFF4C5D70),
        bottom = Color(0xFFB7C3D0),
        accent = Color(0xFFD0DCE8),
        particle = Color(0xE6F4F8FF),
        glow = Color(0x33000000),
    )
    private val heavyDark = Atmosphere(
        top = Color(0xFF141E2A),
        bottom = Color(0xFF070B10),
        accent = Color(0xFF8AA0B5),
        particle = Color(0xD9E7F2FA),
        glow = Color(0x33000000),
    )
    private val storm = Atmosphere(
        top = Color(0xFF121826),
        bottom = Color(0xFF05070C),
        accent = Color(0xFFE8EEF8),
        particle = Color(0xCCE7F0FA),
        glow = Color(0x88F4F7FF),
    )
    private val snowLight = Atmosphere(
        top = Color(0xFFD5E4F2),
        bottom = Color(0xFFF7FBFF),
        accent = Color(0xFFFFFFFF),
        particle = Color(0xFFFFFFFF),
        glow = Color(0x66FFFFFF),
    )
    private val snowDark = Atmosphere(
        top = Color(0xFF243246),
        bottom = Color(0xFF101820),
        accent = Color(0xFFF4F8FF),
        particle = Color(0xFFF7FBFF),
        glow = Color(0x33FFFFFF),
    )
    private val fogLight = Atmosphere(
        top = Color(0xFFC5CED6),
        bottom = Color(0xFFE8EEF2),
        accent = Color(0xFFF7F9FB),
        particle = Color(0xAAFFFFFF),
        glow = Color(0x55FFFFFF),
    )
    private val fogDark = Atmosphere(
        top = Color(0xFF2A333C),
        bottom = Color(0xFF151B22),
        accent = Color(0xFFD5DEE6),
        particle = Color(0x66FFFFFF),
        glow = Color(0x33FFFFFF),
    )
}
