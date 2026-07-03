package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import android.net.Uri

/**
 * Firebase-backed implementation of [IUserProfileRepository].
 *
 * All writes use Firebase's [UserProfileChangeRequest] builder and the
 * [await] coroutine extension from `kotlinx-coroutines-play-services`.
 */
class UserProfileRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IUserProfileRepository {

    private companion object {
        const val USERS_COLLECTION = "user_profiles"
        const val FIELD_BIO = "bio"
    }

    override suspend fun getCurrentProfile(): NetworkResult<UserProfile> {
        val user = firebaseAuth.currentUser
            ?: return NetworkResult.Error.BackendError.NotFound

        return try {
            val profileDoc = firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()

            val bio = profileDoc.getString(FIELD_BIO).orEmpty()

            NetworkResult.Success(
                user.toUserProfile(bio = bio)
            )
        } catch (e: Exception) {
            NetworkResult.Success(
                user.toUserProfile(bio = "")
            )
        }
    }
    override suspend fun updateBio(bio: String): NetworkResult<UserProfile> {
        val user = firebaseAuth.currentUser
            ?: return NetworkResult.Error.BackendError.NotFound

        return try {
            firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .set(
                    mapOf(
                        "uid" to user.uid,
                        "email" to user.email.orEmpty(),
                        "displayName" to user.displayName.orEmpty(),
                        FIELD_BIO to bio
                    ),
                    SetOptions.merge()
                )
                .await()

            NetworkResult.Success(
                user.toUserProfile(bio = bio)
            )
        } catch (e: Exception) {
            NetworkResult.Error.OfflineError
        }
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

    private fun com.google.firebase.auth.FirebaseUser.toUserProfile(
        bio: String = ""
    ) = UserProfile(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        bio = bio
    )
    override suspend fun updateAvatar(photoUrl: String): NetworkResult<UserProfile> {
        val user = firebaseAuth.currentUser
            ?: return NetworkResult.Error.BackendError.NotFound

        return try {
            val request = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(photoUrl))
                .build()

            user.updateProfile(request).await()
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

}
