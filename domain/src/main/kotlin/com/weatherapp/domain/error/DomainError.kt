package com.weatherapp.domain.error

sealed class DomainError : Exception() {
    data object NoInternet : DomainError()

    data object Timeout : DomainError()

    data object InvalidResponse : DomainError()

    data object InvalidCoordinates : DomainError()

    data class Api(val code: Int) : DomainError()

    data object LocationPermissionDenied : DomainError()

    data object LocationServicesDisabled : DomainError()

    data object LocationUnavailable : DomainError()

    data object Database : DomainError()

    data object Unknown : DomainError()
}
