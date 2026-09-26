package com.weatherapp.data.mapper

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

object OpenMeteoTime {
    fun parse(value: String?, offsetSeconds: Int): Long? {
        if (value.isNullOrBlank()) return null
        val offset = ZoneOffset.ofTotalSeconds(offsetSeconds)
        return try {
            if (value.contains('T')) {
                LocalDateTime.parse(value).toInstant(offset).toEpochMilli()
            } else {
                LocalDate.parse(value).atStartOfDay().toInstant(offset).toEpochMilli()
            }
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
