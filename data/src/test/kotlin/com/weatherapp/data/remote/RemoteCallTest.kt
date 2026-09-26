package com.weatherapp.data.remote

import com.weatherapp.domain.error.DomainError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RemoteCallTest {
    @Test
    fun mapsTimeout() = runTest {
        val result = remoteCall<String> { throw SocketTimeoutException("timed out") }
        assertTrue(result.exceptionOrNull() is DomainError.Timeout)
    }

    @Test
    fun mapsIoFailureToNoInternet() = runTest {
        val result = remoteCall<String> { throw IOException("offline") }
        assertTrue(result.exceptionOrNull() is DomainError.NoInternet)
    }

    @Test
    fun mapsHttpStatus() = runTest {
        val response = Response.error<String>(503, "nope".toResponseBody("text/plain".toMediaType()))
        val result = remoteCall<String> { throw HttpException(response) }
        val error = result.exceptionOrNull()
        assertTrue(error is DomainError.Api && error.code == 503)
    }

    @Test
    fun mapsInvalidPayload() = runTest {
        val result = remoteCall<String> { throw SerializationException("bad json") }
        assertTrue(result.exceptionOrNull() is DomainError.InvalidResponse)
    }

    @Test(expected = CancellationException::class)
    fun rethrowsCancellation() = runTest {
        remoteCall<String> { throw CancellationException("cancelled") }
    }
}
