package com.weatherapp.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.presentation.R
import com.weatherapp.presentation.components.LoadingSkeleton
import com.weatherapp.presentation.components.MessageState
import com.weatherapp.presentation.message.UserMessage
import com.weatherapp.presentation.message.text
import com.weatherapp.presentation.weather.WeatherDashboard

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        viewModel.onPermissionResolved(result.toPermissionStatus(context))
    }
    var asked by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        when {
            context.hasLocationPermission() -> viewModel.onPermissionResolved(LocationPermissionStatus.Granted)
            !asked -> {
                asked = true
                launcher.launch(LOCATION_PERMISSIONS)
            }
            else -> viewModel.onPermissionResolved(context.currentPermissionStatus())
        }
    }
    WeatherScaffold {
        when {
            state.isLoading && state.weather == null -> LoadingSkeleton()
            state.weather != null -> WeatherDashboard(
                weather = state.weather,
                forecast = state.forecast,
                isRefreshing = state.isRefreshing,
                isFavorite = state.isFavorite,
                onRefresh = viewModel::refresh,
                onToggleFavorite = viewModel::toggleFavorite,
                showBack = false,
                onBack = {},
            )
            else -> HomeEmpty(
                state = state,
                onRequestPermission = { launcher.launch(LOCATION_PERMISSIONS) },
                onOpenSettings = { context.startActivity(appSettingsIntent(context)) },
                onOpenLocationSettings = {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                },
                onRetry = viewModel::refresh,
                onOpenSearch = onOpenSearch,
            )
        }
    }
}

@Composable
private fun HomeEmpty(
    state: HomeUiState,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onRetry: () -> Unit,
    onOpenSearch: () -> Unit,
) {
    val permissionCopy = when (state.permission) {
        LocationPermissionStatus.PermanentlyDenied -> stringResource(R.string.home_permission_permanent)
        else -> stringResource(R.string.home_permission_body)
    }
    val title = when (state.error) {
        UserMessage.LocationServices -> stringResource(R.string.error_location_services)
        UserMessage.LocationUnavailable -> stringResource(R.string.error_location_unavailable)
        UserMessage.LocationPermission, null -> stringResource(R.string.home_permission_title)
        else -> stringResource(R.string.home_empty_title)
    }
    val body = state.error?.text() ?: permissionCopy
    val primary = when {
        state.permission == LocationPermissionStatus.PermanentlyDenied ->
            stringResource(R.string.action_open_settings) to onOpenSettings
        state.error == UserMessage.LocationServices ->
            stringResource(R.string.action_open_location_settings) to onOpenLocationSettings
        state.permission != LocationPermissionStatus.Granted ->
            stringResource(R.string.action_grant_location) to onRequestPermission
        state.error != null -> stringResource(R.string.action_retry) to onRetry
        else -> null
    }
    MessageState(
        title = title,
        body = body,
        primaryLabel = primary?.first,
        onPrimary = primary?.second,
        secondaryLabel = stringResource(R.string.action_search_cities),
        onSecondary = onOpenSearch,
    )
}

@Composable
internal fun WeatherScaffold(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) { content() }
}

private fun Map<String, Boolean>.toPermissionStatus(context: Context): LocationPermissionStatus {
    if (this[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
        this[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    ) {
        return LocationPermissionStatus.Granted
    }
    val activity = context.findActivity()
    val canAskAgain = activity != null && LOCATION_PERMISSIONS.any { permission ->
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    return if (canAskAgain) LocationPermissionStatus.Denied else LocationPermissionStatus.PermanentlyDenied
}

private fun Context.currentPermissionStatus(): LocationPermissionStatus {
    if (hasLocationPermission()) return LocationPermissionStatus.Granted
    val activity = findActivity()
    val canAskAgain = activity != null && LOCATION_PERMISSIONS.any { permission ->
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    return if (canAskAgain) LocationPermissionStatus.Denied else LocationPermissionStatus.PermanentlyDenied
}

private fun Context.hasLocationPermission(): Boolean {
    return LOCATION_PERMISSIONS.any { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun appSettingsIntent(context: Context): Intent {
    return Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )
}

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)
