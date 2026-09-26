package com.weatherapp.data.mapper

import com.weatherapp.domain.model.WeatherCondition

object WeatherCodeMapper {
    fun map(code: Int?): WeatherCondition = when (code) {
        0, 1 -> WeatherCondition.CLEAR
        2 -> WeatherCondition.PARTLY_CLOUDY
        3 -> WeatherCondition.CLOUDY
        45, 48 -> WeatherCondition.FOG
        51, 53, 56, 61, 63, 66, 80, 81 -> WeatherCondition.RAIN
        55, 57, 65, 67, 82 -> WeatherCondition.HEAVY_RAIN
        71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOW
        95, 96, 99 -> WeatherCondition.THUNDERSTORM
        else -> WeatherCondition.UNKNOWN
    }
}
