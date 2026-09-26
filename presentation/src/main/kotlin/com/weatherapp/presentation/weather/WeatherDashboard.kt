package com.weatherapp.presentation.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.weatherapp.domain.model.Forecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.presentation.theme.Dimens
import com.weatherapp.presentation.weather.atmosphere.WeatherBackdrop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeatherDashboard(
    weather: Weather,
    forecast: Forecast?,
    isRefreshing: Boolean,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        WeatherBackdrop(condition = weather.condition, modifier = Modifier.fillMaxSize())
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                if (maxWidth >= Dimens.expandedWidth) {
                    ExpandedForecast(weather, forecast, isFavorite, onRefresh, onToggleFavorite, showBack, onBack)
                } else {
                    CompactForecast(weather, forecast, isFavorite, onRefresh, onToggleFavorite, showBack, onBack)
                }
            }
        }
    }
}

@Composable
private fun CompactForecast(
    weather: Weather,
    forecast: Forecast?,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.sectionGap),
    ) {
        item { hero(weather, isFavorite, onToggleFavorite, onRefresh, showBack, onBack) }
        item { MetricGrid(weather) }
        item { HourlyForecastRow(forecast?.hourly.orEmpty(), weather.city.timezone) }
        item { DailyForecastList(forecast?.daily.orEmpty(), weather.city.timezone) }
    }
}

@Composable
private fun ExpandedForecast(
    weather: Weather,
    forecast: Forecast?,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.sectionGap),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.sectionGap),
        ) {
            item { hero(weather, isFavorite, onToggleFavorite, onRefresh, showBack, onBack) }
            item { MetricGrid(weather) }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.sectionGap),
        ) {
            item { HourlyForecastRow(forecast?.hourly.orEmpty(), weather.city.timezone) }
            item { DailyForecastList(forecast?.daily.orEmpty(), weather.city.timezone) }
        }
    }
}

@Composable
private fun hero(
    weather: Weather,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    WeatherHero(weather, isFavorite, onToggleFavorite, onRefresh, showBack, onBack)
}

internal fun placeLabel(name: String, adminArea: String, country: String, fallback: String): String {
    val title = name.ifBlank { fallback }
    val region = listOf(adminArea, country).filter { it.isNotBlank() && !it.equals(title, ignoreCase = true) }
    return if (region.isEmpty()) title else "$title, ${region.joinToString(", ")}"
}
