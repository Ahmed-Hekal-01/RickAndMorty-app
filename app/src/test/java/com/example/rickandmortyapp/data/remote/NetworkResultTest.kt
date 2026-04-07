package com.example.rickandmortyapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultTest {

    @Test
    fun safeApiCall_returnsSuccessWhenBlockSucceeds() = runBlocking {
        val result = safeApiCall { "ok" }

        assertTrue(result is NetworkResult.Success)
        assertEquals("ok", (result as NetworkResult.Success).data)
    }

    @Test
    fun safeApiCall_mapsIOExceptionToOfflineError() = runBlocking {
        val result = safeApiCall<String> { throw IOException("No internet") }

        assertEquals(NetworkResult.Error.OfflineError, result)
    }

    @Test
    fun safeApiCall_maps404ToNotFound() = runBlocking {
        val client = mockClient(HttpStatusCode.NotFound)

        val result = safeApiCall { client.get("https://example.com/fail") }

        assertEquals(NetworkResult.Error.BackendError.NotFound, result)
    }

    @Test
    fun safeApiCall_maps429ToTooManyRequests() = runBlocking {
        val client = mockClient(HttpStatusCode.TooManyRequests)

        val result = safeApiCall { client.get("https://example.com/fail") }

        assertEquals(NetworkResult.Error.BackendError.TooManyRequests, result)
    }

    @Test
    fun safeApiCall_maps503ToUnavailable() = runBlocking {
        val client = mockClient(HttpStatusCode.ServiceUnavailable)

        val result = safeApiCall { client.get("https://example.com/fail") }

        assertEquals(NetworkResult.Error.BackendError.Unavailable, result)
    }

    @Test
    fun safeApiCall_mapsUnexpectedBackendStatusToUnknown() = runBlocking {
        val client = mockClient(HttpStatusCode.InternalServerError)

        val result = safeApiCall { client.get("https://example.com/fail") }

        assertEquals(NetworkResult.Error.BackendError.UnKnown, result)
    }

    @Test
    fun safeApiCall_mapsUnexpectedExceptionToUnknown() = runBlocking {
        val result = safeApiCall<String> { throw IllegalStateException("boom") }

        assertEquals(NetworkResult.Error.BackendError.UnKnown, result)
    }

    private fun mockClient(statusCode: HttpStatusCode) = HttpClient(MockEngine { _ ->
        respond(
            content = "{}",
            status = statusCode,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }) {
        expectSuccess = true
    }
}

