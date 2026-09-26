package com.weatherapp.presentation.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weatherapp.domain.model.DailyForecast
import com.weatherapp.domain.model.HourlyForecast
import com.weatherapp.domain.model.Weather
import com.weatherapp.presentation.R
import com.weatherapp.presentation.format.WeatherFormatters
import com.weatherapp.presentation.theme.Dimens
import com.weatherapp.presentation.theme.TypeScale

@Composable
internal fun WeatherHero(
    weather: Weather,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
) {
    val condition = stringResource(weather.condition.labelRes())
    val temperature = WeatherFormatters.temperature(weather.temperatureCelsius)
    val description = stringResource(R.string.cd_temperature, temperature, condition)
    val place = placeLabel(
        name = weather.city.name,
        adminArea = weather.city.adminArea,
        country = weather.city.country,
        fallback = stringResource(R.string.current_location_name),
    )
    Card(
        shape = RoundedCornerShape(Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = CARD_ALPHA)),
    ) {
        Column(Modifier.padding(Dimens.cardPadding), verticalArrangement = Arrangement.spacedBy(Dimens.gap)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(place, style = MaterialTheme.typography.titleLarge)
                    Text(condition, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = stringResource(R.string.action_refresh))
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(
                            if (isFavorite) R.string.action_favorite_remove else R.string.action_favorite_add,
                        ),
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.gap)) {
                Icon(
                    imageVector = weather.condition.icon(),
                    contentDescription = stringResource(R.string.cd_weather_icon, condition),
                    modifier = Modifier.size(Dimens.heroIcon),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = TypeScale.temperature),
                    modifier = Modifier.semantics { contentDescription = description },
                )
            }
            Text(
                text = stringResource(R.string.feels_like, WeatherFormatters.temperature(weather.feelsLikeCelsius)),
                style = MaterialTheme.typography.titleMedium,
            )
            val range = WeatherFormatters.temperatureRange(weather.minTemperatureCelsius, weather.maxTemperatureCelsius)
            if (range.isNotEmpty()) {
                Text(range, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
            val cachedAt = weather.cachedAtEpochMillis
            if (weather.isFromCache && cachedAt != null) {
                Text(
                    text = stringResource(
                        R.string.cached_banner,
                        WeatherFormatters.clock(cachedAt, weather.city.timezone),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun MetricGrid(weather: Weather) {
    val metrics = listOf(
        stringResource(R.string.metric_humidity) to (weather.humidityPercent?.let(WeatherFormatters::humidity) ?: stringResource(R.string.metric_unavailable)),
        stringResource(R.string.metric_wind) to (weather.windSpeedKmh?.let(WeatherFormatters::wind) ?: stringResource(R.string.metric_unavailable)),
        stringResource(R.string.metric_sunrise) to (weather.sunriseEpochMillis?.let { WeatherFormatters.clock(it, weather.city.timezone) } ?: stringResource(R.string.metric_unavailable)),
        stringResource(R.string.metric_sunset) to (weather.sunsetEpochMillis?.let { WeatherFormatters.clock(it, weather.city.timezone) } ?: stringResource(R.string.metric_unavailable)),
        stringResource(R.string.metric_uv) to (weather.uvIndex?.let(WeatherFormatters::uv) ?: stringResource(R.string.metric_unavailable)),
        stringResource(R.string.metric_precipitation) to (weather.precipitationProbabilityPercent?.let(WeatherFormatters::precipitation) ?: stringResource(R.string.metric_unavailable)),
    )
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.gap)) {
        metrics.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.gap), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (label, value) ->
                    MetricCard(label, value, Modifier.weight(1f))
                }
                if (row.size == 1) {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = CARD_ALPHA)),
    ) {
        Column(Modifier.padding(Dimens.cardPadding), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
internal fun HourlyForecastRow(hours: List<HourlyForecast>, timezone: String) {
    if (hours.isEmpty()) return
    Card(
        shape = RoundedCornerShape(Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = CARD_ALPHA)),
    ) {
        Column(Modifier.padding(Dimens.cardPadding), verticalArrangement = Arrangement.spacedBy(Dimens.gap)) {
            Text(stringResource(R.string.section_hourly), style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.gap)) {
                items(hours, key = { it.timeEpochMillis }) { hour ->
                    HourChip(hour, timezone)
                }
            }
        }
    }
}

@Composable
private fun HourChip(hour: HourlyForecast, timezone: String) {
    val condition = stringResource(hour.condition.labelRes())
    val label = stringResource(
        R.string.cd_hourly,
        WeatherFormatters.hour(hour.timeEpochMillis, timezone),
        WeatherFormatters.temperature(hour.temperatureCelsius),
        condition,
    )
    Column(
        modifier = Modifier
            .width(Dimens.hourlyCardWidth)
            .semantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(WeatherFormatters.hour(hour.timeEpochMillis, timezone), style = MaterialTheme.typography.labelLarge)
        Icon(
            imageVector = hour.condition.icon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(WeatherFormatters.temperature(hour.temperatureCelsius), style = MaterialTheme.typography.titleMedium)
        Text(
            hour.precipitationProbabilityPercent?.let(WeatherFormatters::precipitation).orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun DailyForecastList(days: List<DailyForecast>, timezone: String) {
    if (days.isEmpty()) return
    Card(
        shape = RoundedCornerShape(Dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = CARD_ALPHA)),
    ) {
        Column(Modifier.padding(Dimens.cardPadding), verticalArrangement = Arrangement.spacedBy(Dimens.gap)) {
            Text(stringResource(R.string.section_daily), style = MaterialTheme.typography.titleMedium)
            days.forEach { day ->
                DailyRow(day, timezone)
            }
        }
    }
}

@Composable
private fun DailyRow(day: DailyForecast, timezone: String) {
    val condition = stringResource(day.condition.labelRes())
    val description = stringResource(
        R.string.cd_daily,
        WeatherFormatters.day(day.dateEpochMillis, timezone),
        WeatherFormatters.temperature(day.minTemperatureCelsius),
        WeatherFormatters.temperature(day.maxTemperatureCelsius),
        condition,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(WeatherFormatters.day(day.dateEpochMillis, timezone), modifier = Modifier.weight(1f))
        Icon(day.condition.icon(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            day.precipitationProbabilityPercent?.let(WeatherFormatters::precipitation) ?: stringResource(R.string.metric_unavailable),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "${WeatherFormatters.temperature(day.minTemperatureCelsius)}  ${WeatherFormatters.temperature(day.maxTemperatureCelsius)}",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private const val CARD_ALPHA = 0.94f
