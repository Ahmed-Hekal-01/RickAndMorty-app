package com.example.rickandmortyapp.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contract for persisting session-related state across app launches.
 *
 * Backed by [androidx.datastore.preferences.core.Preferences] DataStore so values
 * survive process death without needing a database.
 */
interface ISessionRepository {

    /** Emits true once the user has completed onboarding. */
    val isOnboarded: Flow<Boolean>

    /**
     * Emits the last persisted Firebase ID token, or null if none is stored.
     * Used by [SplashViewModel] to determine the initial navigation destination.
     */
    val authToken: Flow<String?>

    /** Mark onboarding as complete. */
    suspend fun setOnboarded(value: Boolean)

    /** Persist the user's Firebase ID token after a successful login/register. */
    suspend fun saveAuthToken(token: String?)

    /** Wipe all session data on logout. */
    suspend fun clearSession()
}
