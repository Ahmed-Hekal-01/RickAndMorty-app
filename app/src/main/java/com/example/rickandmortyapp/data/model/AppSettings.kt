package com.example.rickandmortyapp.data.model

/**
 * Represents persisted user preferences (dark mode, notifications, etc.).
 * Stored via DataStore and exposed as a Flow by [ISettingsRepository].
 */
data class AppSettings(
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true
)