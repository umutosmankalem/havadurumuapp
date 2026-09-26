package com.weatherapp.presentation.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.presentation.R
import com.weatherapp.presentation.components.LoadingSkeleton
import com.weatherapp.presentation.components.MessageState
import com.weatherapp.presentation.home.WeatherScaffold
import com.weatherapp.presentation.message.text
import com.weatherapp.presentation.weather.WeatherDashboard

@Composable
fun WeatherDetailsScreen(
    onBack: () -> Unit,
    viewModel: WeatherDetailsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    WeatherScaffold {
        when {
            state.isLoading && state.weather == null -> LoadingSkeleton(Modifier.fillMaxSize())
            state.weather != null -> WeatherDashboard(
                weather = state.weather,
                forecast = state.forecast,
                isRefreshing = state.isRefreshing,
                isFavorite = state.isFavorite,
                onRefresh = viewModel::refresh,
                onToggleFavorite = viewModel::toggleFavorite,
                showBack = true,
                onBack = onBack,
            )
            else -> MessageState(
                title = stringResource(R.string.home_empty_title),
                body = state.error?.text() ?: stringResource(R.string.error_unknown),
                primaryLabel = stringResource(R.string.action_retry),
                onPrimary = viewModel::refresh,
                secondaryLabel = stringResource(R.string.action_back),
                onSecondary = onBack,
            )
        }
    }
}
