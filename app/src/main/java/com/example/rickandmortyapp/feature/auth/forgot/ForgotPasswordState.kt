package com.example.rickandmortyapp.feature.auth.forgot

import com.example.rickandmortyapp.feature.base.UiState

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
) : UiState