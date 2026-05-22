package com.example.rickandmortyapp.data.repository

import androidx.lifecycle.ViewModelProvider
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase-backed implementation of [IAuthRepository].
 *
 * Uses [callbackFlow] to turn the Firebase [FirebaseAuth.AuthStateListener] into a cold
 * [Flow] that emits whenever the auth session changes (login / logout / token refresh).
 * All suspend functions use `kotlinx-coroutines-play-services` [await] to bridge the
 * Firebase [Task] API into coroutines.
 */
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IAuthRepository {

    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error.BackendError.UnKnown
            NetworkResult.Success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: FirebaseAuthException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: Exception) {
            NetworkResult.Error.OfflineError
        }
    }

    override suspend fun loginWithGoogle(idToken: String): NetworkResult<FirebaseUser>{
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return NetworkResult.Error.BackendError.UnKnown
            NetworkResult.Success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: FirebaseAuthException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: Exception) {
            NetworkResult.Error.OfflineError
        }
    }

    override suspend fun register(email: String, password: String): NetworkResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error.BackendError.UnKnown
            NetworkResult.Success(user)
        } catch (e: FirebaseAuthWeakPasswordException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already in use
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: FirebaseAuthException) {
            NetworkResult.Error.BackendError.UnKnown
        } catch (e: Exception) {
            NetworkResult.Error.OfflineError
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
