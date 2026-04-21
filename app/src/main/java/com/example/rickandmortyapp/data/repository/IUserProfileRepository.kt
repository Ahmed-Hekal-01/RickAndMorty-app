package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.data.remote.NetworkResult

/**
 * Contract for reading and updating the authenticated user's profile.
 * All data is sourced from Firebase Authentication.
 */
interface IUserProfileRepository {

    /**
     * Returns the current user's profile.
     * Fails with [NetworkResult.Error] if no user is signed in.
     */
    suspend fun getCurrentProfile(): NetworkResult<UserProfile>

    /**
     * Update the display name for the current user.
     * @return the updated [UserProfile] on success.
     */
    suspend fun updateDisplayName(name: String): NetworkResult<UserProfile>
}
