package com.weatherapp.presentation.format

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.roundToInt

internal object WeatherFormatters {
    fun temperature(celsius: Double): String = "${celsius.roundToInt()}°"

    fun temperatureRange(min: Double?, max: Double?): String {
        if (min == null || max == null) return ""
        return "${min.roundToInt()}° / ${max.roundToInt()}°"
    }

    fun humidity(percent: Int): String = "$percent%"

    fun wind(kilometersPerHour: Double): String = "${kilometersPerHour.roundToInt()} km/h"

    fun uv(index: Double): String = index.roundToInt().toString()

    fun precipitation(percent: Int): String = "$percent%"

    fun hour(epochMillis: Long, timezone: String): String = format(epochMillis, timezone, HOUR)

    fun day(epochMillis: Long, timezone: String): String = format(epochMillis, timezone, DAY)

    fun clock(epochMillis: Long, timezone: String): String = format(epochMillis, timezone, CLOCK)

    private fun format(epochMillis: Long, timezone: String, formatter: DateTimeFormatter): String {
        val zone = runCatching { ZoneId.of(timezone) }.getOrElse { ZoneId.systemDefault() }
        return formatter.withZone(zone).format(Instant.ofEpochMilli(epochMillis))
    }

    private val HOUR = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private val DAY = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val CLOCK = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
}
