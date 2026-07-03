package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.AppSettings
import kotlinx.coroutines.flow.Flow

/**
 * Contract for reading and persisting user app settings.
 * Backed by DataStore so preferences survive process death.
 */
interface ISettingsRepository {

    /** Emits the current [AppSettings] and every subsequent change. */
    val settings: Flow<AppSettings>

    /** Persist the dark mode preference. */
    suspend fun setDarkMode(enabled: Boolean)

    /** Persist the notifications preference. */
    suspend fun setNotificationsEnabled(enabled: Boolean)

    /** Persist the language preference ("en" or "ar"). */
    suspend fun setLanguage(languageCode: String)
}
