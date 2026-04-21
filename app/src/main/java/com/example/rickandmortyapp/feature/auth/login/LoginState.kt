package com.example.rickandmortyapp.feature.auth.login

import com.example.rickandmortyapp.feature.base.UiState

/**
 * Full state for the login screen.
 *
 * @param email current email field value.
 * @param password current password field value.
 * @param isLoading true while the login network call is in progress.
 * @param emailError inline validation error for the email field, or null.
 * @param passwordError inline validation error for the password field, or null.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) : UiState
