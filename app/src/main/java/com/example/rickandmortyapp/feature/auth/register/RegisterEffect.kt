package com.example.rickandmortyapp.feature.auth.register

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [RegisterViewModel]. */
sealed class RegisterEffect : UiEffect {
    data object NavigateToHome : RegisterEffect()
    data object NavigateToLogin : RegisterEffect()
    data class ShowError(val message: String) : RegisterEffect()
}
