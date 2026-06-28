package com.example.rickandmortyapp.feature.auth.login

import com.example.rickandmortyapp.feature.base.UiState

/**
 * Full state for the login screen.
 *
 * @param email current email field value.
 * @param password current password field value.
 * @param isEmailLoading true while email/password login is in progress.
 * @param isGoogleLoading true while Google sign-in is in progress.
 * @param emailError inline validation error for the email field, or null.
 * @param passwordError inline validation error for the password field, or null.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isEmailLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) : UiState
