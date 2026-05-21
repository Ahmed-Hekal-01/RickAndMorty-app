package com.example.rickandmortyapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.rickandmortyapp.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

/**
 * DataStore-backed implementation of [ISettingsRepository].
 *
 * Receives a [Named] DataStore so the DI module can provide a distinct
 * instance separate from the session DataStore.
 */
class SettingsRepository @Inject constructor(
    @param:Named("settings") private val dataStore: DataStore<Preferences>
) : ISettingsRepository {

    private companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }

    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            darkMode = prefs[KEY_DARK_MODE] ?: false,
            notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true
        )
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_DARK_MODE] = enabled }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_NOTIFICATIONS] = enabled }
    }
}
