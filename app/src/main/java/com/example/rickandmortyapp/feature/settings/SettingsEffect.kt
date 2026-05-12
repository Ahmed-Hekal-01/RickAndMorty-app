package com.example.rickandmortyapp.feature.settings

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [SettingsViewModel]. */
sealed class SettingsEffect : UiEffect {
    data class ShowError(val message: String) : SettingsEffect()
}
