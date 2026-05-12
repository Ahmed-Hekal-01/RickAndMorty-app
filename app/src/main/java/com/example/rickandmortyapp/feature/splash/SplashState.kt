package com.example.rickandmortyapp.feature.splash

import com.example.rickandmortyapp.feature.base.UiState

/** The two possible launch destinations resolved by the splash screen. */
enum class Destination { LOGIN, HOME }

/**
 * UI state for the splash screen.
 *
 * @param isLoading true while the auth/session check is in progress.
 * @param destination resolved launch destination; null while loading.
 */
data class SplashState(
    val isLoading: Boolean = true,
    val destination: Destination? = null
) : UiState
