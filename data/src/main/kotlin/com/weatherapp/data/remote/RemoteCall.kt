package com.weatherapp.data.remote

import com.weatherapp.domain.error.DomainError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.io.IOException

suspend fun <T> remoteCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: SocketTimeoutException) {
        Result.failure(DomainError.Timeout)
    } catch (_: InterruptedIOException) {
        Result.failure(DomainError.Timeout)
    } catch (http: HttpException) {
        Result.failure(DomainError.Api(http.code()))
    } catch (_: SerializationException) {
        Result.failure(DomainError.InvalidResponse)
    } catch (_: IOException) {
        Result.failure(DomainError.NoInternet)
    } catch (_: Exception) {
        Result.failure(DomainError.Unknown)
    }
}
