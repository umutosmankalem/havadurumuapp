package com.weatherapp.presentation.message

import com.weatherapp.domain.error.DomainError

enum class UserMessage {
    NoInternet,
    Timeout,
    InvalidResponse,
    Api,
    InvalidCoordinates,
    LocationPermission,
    LocationServices,
    LocationUnavailable,
    Database,
    Unknown,
}

internal fun Throwable.toUserMessage(): UserMessage = when (this) {
    DomainError.NoInternet -> UserMessage.NoInternet
    DomainError.Timeout -> UserMessage.Timeout
    DomainError.InvalidResponse -> UserMessage.InvalidResponse
    is DomainError.Api -> UserMessage.Api
    DomainError.InvalidCoordinates -> UserMessage.InvalidCoordinates
    DomainError.LocationPermissionDenied -> UserMessage.LocationPermission
    DomainError.LocationServicesDisabled -> UserMessage.LocationServices
    DomainError.LocationUnavailable -> UserMessage.LocationUnavailable
    DomainError.Database -> UserMessage.Database
    else -> UserMessage.Unknown
}
