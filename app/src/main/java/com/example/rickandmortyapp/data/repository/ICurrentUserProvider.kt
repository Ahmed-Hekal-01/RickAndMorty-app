package com.example.rickandmortyapp.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Provides the currently authenticated user's ID as a reactive [Flow].
 *
 * **Auth decoupling contract:**
 * The Favorites feature depends on this interface — NOT on [IAuthRepository]
 * or Firebase directly. This one-liner interface is the seam that lets the
 * auth team plug in their implementation without touching any favorites code.
 *
 * **Null semantics:**
 * - `null`  → no user is logged in; the favorites system uses
 *             [com.example.rickandmortyapp.data.local.entity.FavoriteCharacterEntity.ANONYMOUS_USER_ID]
 *             and works in local-only mode.
 * - Non-null → a logged-in user; favorites are partitioned by this ID in
 *              both Room and Firestore.
 *
 * **How your teammate wires this in:**
 * ```kotlin
 * // In the auth module's DI module:
 * @Provides @Singleton
 * fun provideCurrentUserProvider(auth: IAuthRepository): ICurrentUserProvider =
 *     object : ICurrentUserProvider {
 *         override val currentUserId: Flow<String?> =
 *             auth.currentUser.map { it?.uid }
 *     }
 * ```
 */
interface ICurrentUserProvider {
    /** Emits the Firebase UID of the signed-in user, or `null` if anonymous. */
    val currentUserId: Flow<String?>
}
