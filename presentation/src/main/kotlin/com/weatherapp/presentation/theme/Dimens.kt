package com.weatherapp.presentation.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal object Dimens {
    val screenPadding = 20.dp
    val cardRadius = 28.dp
    val cardPadding = 16.dp
    val gap = 12.dp
    val sectionGap = 20.dp
    val touchTarget = 48.dp
    val expandedWidth = 840.dp
    val hourlyCardWidth = 84.dp
    val metricMinHeight = 96.dp
    val iconSize = 28.dp
    val heroIcon = 42.dp
}

internal object Motion {
    const val CYCLE_MILLIS = 12_000
    const val RAIN_DROP_COUNT = 28
    const val SNOWFLAKE_COUNT = 32
    const val CLOUD_COUNT = 4
    const val SEARCH_DEBOUNCE_MILLIS = 350L
    const val CONTENT_FADE_MILLIS = 280
}

internal object TypeScale {
    val temperature = 88.sp
}
