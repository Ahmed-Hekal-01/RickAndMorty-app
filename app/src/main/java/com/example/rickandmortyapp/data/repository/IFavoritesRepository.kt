package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.Character
import kotlinx.coroutines.flow.Flow

/**
 * Contract for the Favorites feature.
 *
 * **Sign-in requirement:**
 * The app requires sign-in. All operations are no-ops (or return empty flows)
 * when no user is authenticated. In practice this state only occurs briefly on
 * cold start before the auth session is restored.
 *
 * **Single Source of Truth:**
 * [observeFavorites], [observeFavoriteIds], and [observeIsFavorite] return
 * Room-backed [Flow]s. Every write — from any screen — causes Room to re-emit
 * and all collectors update simultaneously with no manual coordination needed.
 *
 * **Bidirectional cloud sync:**
 * • Local → Cloud: every [toggleFavorite] write is mirrored to Firestore
 *   (best-effort, retried by [syncPendingToCloud]).
 * • Cloud → Local: [FavoritesRepository] automatically pulls from Firestore
 *   on every login, so a fresh-install user always sees their existing favorites.
 */
interface IFavoritesRepository {

    /** Live list of the current user's favourite characters, newest first. */
    val observeFavorites: Flow<List<Character>>

    /**
     * Live set of favourite character IDs for the current user.
     * O(1) [Set.contains] lookup per card in the Home screen.
     */
    val observeFavoriteIds: Flow<Set<Int>>

    /**
     * Emits `true` when [characterId] is in the current user's favourites.
     * Used by the Detail screen heart icon.
     */
    fun observeIsFavorite(characterId: Int): Flow<Boolean>

    /**
     * Toggle a character's favourite status.
     * Writes to Room first (instant UI), then syncs to Firestore in the background.
     */
    suspend fun toggleFavorite(character: Character)

    /**
     * Retries all locally-stored rows where [isSyncedToCloud] is false.
     * Safe to call from a WorkManager connectivity-restored task.
     */
    suspend fun syncPendingToCloud()
}
