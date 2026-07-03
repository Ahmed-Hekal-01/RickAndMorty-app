package com.example.rickandmortyapp.feature.auth.register

import com.example.rickandmortyapp.feature.base.UiState

/**
 * Full state for the registration screen.
 *
 * @param email current email field value.
 * @param password current password field value.
 * @param confirmPassword repeated password field value.
 * @param isLoading true while the register network call is in progress.
 * @param emailError inline validation error for the email field, or null.
 * @param passwordError inline validation error for the password field, or null.
 * @param confirmPasswordError inline validation error for confirm field, or null.
 */
data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) : UiState