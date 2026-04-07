package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.remote.NetworkResult

inline fun <T, R> NetworkResult<T>.mapSuccess(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(this.data))
        is NetworkResult.Error -> this
    }
}