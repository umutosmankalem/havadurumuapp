package com.weatherapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.weatherapp.data.remote.OpenMeteoParams
import com.weatherapp.domain.error.DomainError
import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.UserLocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FusedUserLocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient,
    private val permissionChecker: LocationPermissionChecker,
) : UserLocationRepository {
    override suspend fun currentCity(): Result<City> {
        if (!permissionChecker.hasLocationPermission()) {
            return Result.failure(DomainError.LocationPermissionDenied)
        }
        if (!locationServicesEnabled()) {
            return Result.failure(DomainError.LocationServicesDisabled)
        }
        val location = try {
            withTimeoutOrNull(OpenMeteoParams.LOCATION_TIMEOUT_MILLIS) { currentLocation() }
        } catch (_: SecurityException) {
            return Result.failure(DomainError.LocationPermissionDenied)
        } catch (_: Exception) {
            return Result.failure(DomainError.LocationUnavailable)
        }
        if (location == null) return Result.failure(DomainError.LocationUnavailable)
        return Result.success(reverseGeocode(location.latitude, location.longitude))
    }

    private fun locationServicesEnabled(): Boolean {
        val manager = context.getSystemService(LocationManager::class.java) ?: return false
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation() = suspendCancellableCoroutine { continuation ->
        val tokenSource = CancellationTokenSource()
        continuation.invokeOnCancellation { tokenSource.cancel() }
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, tokenSource.token)
            .addOnSuccessListener { location ->
                if (continuation.isActive) continuation.resume(location)
            }
            .addOnFailureListener {
                if (continuation.isActive) continuation.resume(null)
            }
    }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): City {
        val fallback = City(
            name = "",
            country = "",
            countryCode = "",
            adminArea = "",
            latitude = latitude,
            longitude = longitude,
            timezone = java.time.ZoneId.systemDefault().id,
            geocodingId = null,
        )
        if (!Geocoder.isPresent()) return fallback
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { results ->
                        if (continuation.isActive) continuation.resume(results)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1).orEmpty()
            }
        } catch (_: Exception) {
            emptyList()
        }
        val address = addresses.firstOrNull() ?: return fallback
        val name = address.locality ?: address.subAdminArea ?: address.adminArea ?: ""
        return fallback.copy(
            name = name,
            country = address.countryName.orEmpty(),
            countryCode = address.countryCode.orEmpty(),
            adminArea = address.adminArea.orEmpty(),
        )
    }
}
