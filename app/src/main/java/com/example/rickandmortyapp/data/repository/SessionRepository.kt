package com.example.rickandmortyapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

/**
 * DataStore-backed implementation of [ISessionRepository].
 *
 * Receives a [Named] DataStore so the DI module can provide a distinct
 * instance separate from the settings DataStore.
 */
class SessionRepository @Inject constructor(
    @Named("session") private val dataStore: DataStore<Preferences>
) : ISessionRepository {

    private companion object {
        val KEY_IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    override val isOnboarded: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_IS_ONBOARDED] ?: false }

    override val authToken: Flow<String?> = dataStore.data
        .map { prefs -> prefs[KEY_AUTH_TOKEN] }

    override suspend fun setOnboarded(value: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_IS_ONBOARDED] = value }
    }

    override suspend fun saveAuthToken(token: String?) {
        dataStore.edit { prefs ->
            if (token != null) {
                prefs[KEY_AUTH_TOKEN] = token
            } else {
                prefs.remove(KEY_AUTH_TOKEN)
            }
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}
