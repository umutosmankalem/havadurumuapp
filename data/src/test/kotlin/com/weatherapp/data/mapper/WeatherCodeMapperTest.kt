package com.weatherapp.data.mapper

import com.weatherapp.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCodeMapperTest {
    @Test
    fun mapsOpenMeteoCodesOntoDomainConditions() {
        assertEquals(WeatherCondition.CLEAR, WeatherCodeMapper.map(0))
        assertEquals(WeatherCondition.CLEAR, WeatherCodeMapper.map(1))
        assertEquals(WeatherCondition.PARTLY_CLOUDY, WeatherCodeMapper.map(2))
        assertEquals(WeatherCondition.CLOUDY, WeatherCodeMapper.map(3))
        assertEquals(WeatherCondition.FOG, WeatherCodeMapper.map(45))
        assertEquals(WeatherCondition.FOG, WeatherCodeMapper.map(48))
        assertEquals(WeatherCondition.RAIN, WeatherCodeMapper.map(61))
        assertEquals(WeatherCondition.RAIN, WeatherCodeMapper.map(80))
        assertEquals(WeatherCondition.HEAVY_RAIN, WeatherCodeMapper.map(65))
        assertEquals(WeatherCondition.HEAVY_RAIN, WeatherCodeMapper.map(82))
        assertEquals(WeatherCondition.SNOW, WeatherCodeMapper.map(71))
        assertEquals(WeatherCondition.SNOW, WeatherCodeMapper.map(86))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCodeMapper.map(95))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCodeMapper.map(99))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCodeMapper.map(null))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCodeMapper.map(123))
    }
}
