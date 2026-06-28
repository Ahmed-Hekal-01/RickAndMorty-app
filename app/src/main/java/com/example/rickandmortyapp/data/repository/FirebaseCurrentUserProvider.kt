package com.example.rickandmortyapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Concrete [ICurrentUserProvider] that reads the UID directly from
 * [FirebaseAuth]'s auth-state listener.
 *
 * **Why this exists as a separate class from [AuthRepository]:**
 * The Favorites feature must never import [AuthRepository] — that would
 * create a hard coupling to a module that's still under active development.
 * Instead it depends only on [ICurrentUserProvider], which is this thin adapter.
 *
 * When your teammate finishes the auth module, they can either:
 * a) Keep this class and let it stay (it's already correct), or
 * b) Replace the binding in [com.example.rickandmortyapp.di.FavoritesModule]
 *    with their own implementation without changing a single line of
 *    favorites code.
 *
 * **Flow semantics:**
 * - Emits immediately with the current auth state on first collection.
 * - Re-emits on every login / logout / token change.
 * - Emits `null` when no user is signed in → triggers anonymous/local-only mode.
 */
class FirebaseCurrentUserProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ICurrentUserProvider {

    override val currentUserId: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}
