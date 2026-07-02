package com.example.rickandmortyapp.feature.auth.forgot

import com.example.rickandmortyapp.feature.base.UiEffect

sealed class ForgotPasswordEffect : UiEffect {
    data object NavigateBackToLogin : ForgotPasswordEffect()
    data class ShowMessage(val message: String) : ForgotPasswordEffect()
}