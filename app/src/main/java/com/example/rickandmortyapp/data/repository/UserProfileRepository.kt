package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase-backed implementation of [IUserProfileRepository].
 *
 * All writes use Firebase's [UserProfileChangeRequest] builder and the
 * [await] coroutine extension from `kotlinx-coroutines-play-services`.
 */
class UserProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IUserProfileRepository {

    override suspend fun getCurrentProfile(): NetworkResult<UserProfile> {
        val user = firebaseAuth.currentUser
            ?: return NetworkResult.Error.BackendError.NotFound
        return NetworkResult.Success(user.toUserProfile())
    }

    override suspend fun updateDisplayName(name: String): NetworkResult<UserProfile> {
        val user = firebaseAuth.currentUser
            ?: return NetworkResult.Error.BackendError.NotFound
        return try {
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(request).await()
            // Reload to get fresh data
            user.reload().await()
            val refreshed = firebaseAuth.currentUser
                ?: return NetworkResult.Error.BackendError.UnKnown
            NetworkResult.Success(refreshed.toUserProfile())
        } catch (e: FirebaseAuthException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: Exception) {
            NetworkResult.Error.OfflineError
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun com.google.firebase.auth.FirebaseUser.toUserProfile() = UserProfile(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )
}
