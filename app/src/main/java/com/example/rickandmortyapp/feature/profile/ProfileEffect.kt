package com.example.rickandmortyapp.feature.profile

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [ProfileViewModel]. */
sealed class ProfileEffect : UiEffect {
    data object NavigateToLogin : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    data class ShowSuccess(val message: String) : ProfileEffect()
}
