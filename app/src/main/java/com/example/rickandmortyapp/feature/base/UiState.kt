package com.example.rickandmortyapp.feature.base

/**
 * Marker interface for all MVI UI states.
 *
 * Every feature's state data class must implement this so it can be used
 * as the [S] type parameter in [MviViewModel].
 */
interface UiState
