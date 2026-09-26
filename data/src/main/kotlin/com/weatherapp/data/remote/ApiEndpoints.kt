package com.weatherapp.data.remote

object ApiEndpoints {
    const val FORECAST_BASE_URL = "https://api.open-meteo.com/"
    const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
}

object OpenMeteoParams {
    const val CURRENT = "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m,precipitation"
    const val HOURLY = "temperature_2m,precipitation_probability,weather_code,uv_index"
    const val DAILY = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_probability_max,uv_index_max"
    const val TIMEZONE_AUTO = "auto"
    const val TEMPERATURE_UNIT_CELSIUS = "celsius"
    const val WIND_SPEED_UNIT_KMH = "kmh"
    const val FORECAST_DAYS = 7
    const val HOURLY_STEPS = 24
    const val GEOCODING_RESULT_COUNT = 20
    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
    const val MEMORY_TTL_MILLIS = 60_000L
    const val LOCATION_TIMEOUT_MILLIS = 12_000L
    const val SINGLE_PLACE_ROW_ID = 1
}
