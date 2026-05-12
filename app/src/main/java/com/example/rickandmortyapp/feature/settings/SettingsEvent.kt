package com.example.rickandmortyapp.feature.settings

import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the settings screen can send to [SettingsViewModel]. */
sealed class SettingsEvent : UiEvent {
    data object LoadSettings : SettingsEvent()
    data class ToggleDarkMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent()
}
