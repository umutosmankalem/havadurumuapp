package com.weatherapp.presentation.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weatherapp.presentation.R

@Composable
internal fun UserMessage.text(): String = stringResource(
    when (this) {
        UserMessage.NoInternet -> R.string.error_no_internet
        UserMessage.Timeout -> R.string.error_timeout
        UserMessage.InvalidResponse -> R.string.error_invalid
        UserMessage.Api -> R.string.error_api
        UserMessage.InvalidCoordinates -> R.string.error_coordinates
        UserMessage.LocationPermission -> R.string.error_location_permission
        UserMessage.LocationServices -> R.string.error_location_services
        UserMessage.LocationUnavailable -> R.string.error_location_unavailable
        UserMessage.Database -> R.string.error_database
        UserMessage.Unknown -> R.string.error_unknown
    },
)
