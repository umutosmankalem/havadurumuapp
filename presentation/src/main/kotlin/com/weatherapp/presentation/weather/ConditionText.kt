package com.weatherapp.presentation.weather

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.weatherapp.domain.model.WeatherCondition
import com.weatherapp.presentation.R

internal fun WeatherCondition.labelRes(): Int = when (this) {
    WeatherCondition.CLEAR -> R.string.condition_clear
    WeatherCondition.PARTLY_CLOUDY -> R.string.condition_partly_cloudy
    WeatherCondition.CLOUDY -> R.string.condition_cloudy
    WeatherCondition.RAIN -> R.string.condition_rain
    WeatherCondition.HEAVY_RAIN -> R.string.condition_heavy_rain
    WeatherCondition.THUNDERSTORM -> R.string.condition_thunderstorm
    WeatherCondition.SNOW -> R.string.condition_snow
    WeatherCondition.FOG -> R.string.condition_fog
    WeatherCondition.UNKNOWN -> R.string.condition_unknown
}

internal fun WeatherCondition.icon(): ImageVector = when (this) {
    WeatherCondition.CLEAR -> Icons.Filled.WbSunny
    WeatherCondition.PARTLY_CLOUDY -> Icons.Filled.WbCloudy
    WeatherCondition.CLOUDY -> Icons.Filled.Cloud
    WeatherCondition.RAIN, WeatherCondition.HEAVY_RAIN -> Icons.Filled.WaterDrop
    WeatherCondition.THUNDERSTORM -> Icons.Filled.Thunderstorm
    WeatherCondition.SNOW -> Icons.Filled.AcUnit
    WeatherCondition.FOG -> Icons.Filled.Dehaze
    WeatherCondition.UNKNOWN -> Icons.Filled.QuestionMark
}
