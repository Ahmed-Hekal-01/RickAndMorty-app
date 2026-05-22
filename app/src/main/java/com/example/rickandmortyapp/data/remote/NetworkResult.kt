package com.example.rickandmortyapp.data.remote

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException


sealed class NetworkResult<out T> {
    data class Success<T>(val data : T) : NetworkResult<T>()
    sealed class Error : NetworkResult<Nothing>() {
        sealed class BackendError : Error() {
            data object TooManyRequests : BackendError()
            data object NotFound : BackendError()
            data object Unavailable : BackendError()
            data object UnKnown : BackendError()
        }
        data object OfflineError : Error()
        data object UserCancellation : Error()
    }
}

suspend inline fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(data = block())
    } catch (e: ResponseException) {
        val error = when (e.response.status.value) {
            404 -> NetworkResult.Error.BackendError.NotFound
            429 -> NetworkResult.Error.BackendError.TooManyRequests
            503 -> NetworkResult.Error.BackendError.Unavailable
            else -> NetworkResult.Error.BackendError.UnKnown
        }
        error
    } catch (e: Exception) {
        if (e is IOException) {
            NetworkResult.Error.OfflineError
        } else {
            NetworkResult.Error.BackendError.UnKnown
        }
    }
}