package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Firebase Authentication operations.
 *
 * All suspend functions return [NetworkResult] so callers can handle
 * success/error in a consistent MVI way without catching exceptions directly.
 */
interface IAuthRepository {

    /** Emits the current [FirebaseUser] every time auth state changes. Null = logged out. */
    val currentUser: Flow<FirebaseUser?>

    suspend fun loginWithGoogle(idToken : String) : NetworkResult<Boolean> {
       TODO()
    }
    /** Synchronous check — true if a user session is currently active. */
    val isLoggedIn: Boolean

    /**
     * Attempt email/password login.
     * @return [NetworkResult.Success] with the signed-in [FirebaseUser], or [NetworkResult.Error].
     */
    suspend fun login(email: String, password: String): NetworkResult<FirebaseUser>

    /**
     * Create a new email/password account.
     * @return [NetworkResult.Success] with the created [FirebaseUser], or [NetworkResult.Error].
     */
    suspend fun register(email: String, password: String): NetworkResult<FirebaseUser>

    /** Sign out the current user. Clears the Firebase auth session. */
    suspend fun logout()
}
