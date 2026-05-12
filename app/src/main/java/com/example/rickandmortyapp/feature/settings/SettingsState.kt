package com.example.rickandmortyapp.feature.settings

import com.example.rickandmortyapp.data.model.AppSettings
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the settings screen.
 *
 * @param darkMode current dark mode preference.
 * @param notificationsEnabled current notifications preference.
 * @param isLoading true while reading the initial settings from DataStore.
 */
data class SettingsState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true
) : UiState

/** Convenience constructor from the domain model. */
fun SettingsState.fromSettings(settings: AppSettings) = copy(
    darkMode = settings.darkMode,
    notificationsEnabled = settings.notificationsEnabled,
    isLoading = false
)
