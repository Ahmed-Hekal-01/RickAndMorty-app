package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.local.dao.FavoriteCharacterDao
import com.example.rickandmortyapp.data.local.entity.FavoriteCharacterEntity
import com.example.rickandmortyapp.data.local.mapper.toCharacter
import com.example.rickandmortyapp.data.local.mapper.toFavoriteEntity
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.di.ApplicationScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of [IFavoritesRepository].
 *
 * ─── Data Flow ───────────────────────────────────────────────────────────────
 *
 *   Login detected (userId changes null → UID)
 *       │
 *       ▼
 *   fetchFavoritesFromCloud()          ← Firestore → Room (hydration on login)
 *       │
 *       ▼ Room emits via Flow
 *   observeFavorites / observeFavoriteIds / observeIsFavorite
 *       │
 *       ▼
 *   ViewModels → UI                    ← Room is always the Single Source of Truth
 *
 *   User taps heart
 *       │
 *       ├─ Room write (instant UI update via Flow)
 *       └─ Firestore write (background, best-effort)
 *
 * ─── Sign-in requirement ─────────────────────────────────────────────────────
 * The app requires sign-in. All operations are no-ops when [userId] is null
 * (i.e. the auth session has not yet been established on cold start).
 * In practice the UI is gated behind auth screens so this guard is a safety net.
 *
 * ─── Cloud hydration on login ────────────────────────────────────────────────
 * [fetchFavoritesFromCloud] is called automatically every time a new UID is
 * detected via [ICurrentUserProvider.currentUserId]. This covers:
 * • Fresh install / cleared app data → pulls full cloud list into Room.
 * • Login on a second device         → same behaviour.
 * • Account switch                   → new UID triggers a fresh pull.
 */
@Singleton
class FavoritesRepository @Inject constructor(
    private val dao: FavoriteCharacterDao,
    private val firestore: FirebaseFirestore,
    private val currentUserProvider: ICurrentUserProvider,
    @ApplicationScope private val appScope: CoroutineScope
) : IFavoritesRepository {

    init {
        // Observe user sign-ins for the entire app lifetime and hydrate Room
        // from Firestore whenever a new UID appears.
        // distinctUntilChanged ensures we don't re-fetch for redundant emissions
        // of the same UID, but DOES re-fetch if the user logs out and back in
        // because null is emitted in between, breaking the consecutive equality.
        appScope.launch {
            currentUserProvider.currentUserId
                .distinctUntilChanged()
                .filterNotNull()
                .collect { userId -> fetchFavoritesFromCloud(userId) }
        }
    }

    // ─── Read API ─────────────────────────────────────────────────────────────
    // When userId is null (between logout and login), return empty flows so the
    // UI shows an empty/loading state rather than stale data.

    override val observeFavorites: Flow<List<Character>> =
        currentUserProvider.currentUserId.flatMapLatest { userId ->
            if (userId == null) flowOf(emptyList())
            else dao.observeFavorites(userId).map { it.map(FavoriteCharacterEntity::toCharacter) }
        }

    override val observeFavoriteIds: Flow<Set<Int>> =
        currentUserProvider.currentUserId.flatMapLatest { userId ->
            if (userId == null) flowOf(emptySet())
            else dao.observeFavoriteIds(userId).map { it.toSet() }
        }

    override fun observeIsFavorite(characterId: Int): Flow<Boolean> =
        currentUserProvider.currentUserId.flatMapLatest { userId ->
            if (userId == null) flowOf(false)
            else dao.observeIsFavorite(characterId, userId)
        }

    // ─── Write API ────────────────────────────────────────────────────────────

    override suspend fun toggleFavorite(character: Character) {
        val userId = currentUserProvider.currentUserId.first() ?: return   // guard: must be signed in
        val isFavorite = dao.observeIsFavorite(character.id, userId).first()

        if (isFavorite) {
            dao.deleteFavorite(character.id, userId)
            deleteFromFirestore(character.id, userId)
        } else {
            val entity = character.toFavoriteEntity(userId)
            dao.insertFavorite(entity)
            syncEntityToFirestore(entity)
        }
    }

    override suspend fun syncPendingToCloud() {
        val userId = currentUserProvider.currentUserId.first() ?: return
        val pending = dao.getPendingSyncFavorites(userId)
        Timber.d("FavoritesRepository: syncing ${pending.size} pending rows to Firestore")
        pending.forEach { syncEntityToFirestore(it) }
    }

    // ─── Cloud hydration (Firestore → Room) ───────────────────────────────────

    /**
     * Fetches all of [userId]'s favorites from Firestore and upserts them into
     * Room. Called automatically on every login.
     *
     * Uses [OnConflictStrategy.REPLACE] (via [FavoriteCharacterDao.insertFavorite])
     * so rows already in Room are refreshed with the latest cloud data without
     * duplicating them.
     */
    private suspend fun fetchFavoritesFromCloud(userId: String) {
        Timber.d("FavoritesRepository: fetching favorites from Firestore for user $userId")
        runCatching {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                val entity = FavoriteCharacterEntity(
                    characterId = doc.getLong("characterId")?.toInt() ?: return@forEach,
                    userId       = userId,
                    name         = doc.getString("name")     ?: return@forEach,
                    imageUrl     = doc.getString("imageUrl") ?: "",
                    species      = doc.getString("species")  ?: "",
                    status       = doc.getString("status")   ?: CharacterStatus.UNKNOWN.name,
                    gender       = doc.getString("gender")   ?: "",
                    origin       = doc.getString("origin")   ?: "",
                    location     = doc.getString("location") ?: "",
                    addedAt      = doc.getLong("addedAt")    ?: System.currentTimeMillis(),
                    isSyncedToCloud = true   // came from cloud, already in sync
                )
                dao.insertFavorite(entity)
            }
            Timber.d("FavoritesRepository: hydrated ${snapshot.size()} favorites from Firestore")
        }.onFailure { e ->
            Timber.w(e, "FavoritesRepository: failed to fetch favorites from Firestore for user $userId")
        }
    }

    // ─── Firestore write helpers ──────────────────────────────────────────────

    private suspend fun syncEntityToFirestore(entity: FavoriteCharacterEntity) {
        val data = mapOf(
            "characterId" to entity.characterId,
            "name"        to entity.name,
            "imageUrl"    to entity.imageUrl,
            "species"     to entity.species,
            "status"      to entity.status,
            "gender"      to entity.gender,
            "origin"      to entity.origin,
            "location"    to entity.location,
            "addedAt"     to entity.addedAt
        )
        runCatching {
            firestore
                .collection("users")
                .document(entity.userId)
                .collection("favorites")
                .document(entity.characterId.toString())
                .set(data, SetOptions.merge())
                .await()
            dao.markAsSynced(entity.characterId, entity.userId)
            Timber.d("FavoritesRepository: synced char ${entity.characterId} to Firestore")
        }.onFailure { e ->
            Timber.w(e, "FavoritesRepository: Firestore write failed for char ${entity.characterId}. Will retry.")
        }
    }

    private suspend fun deleteFromFirestore(characterId: Int, userId: String) {
        runCatching {
            firestore
                .collection("users")
                .document(userId)
                .collection("favorites")
                .document(characterId.toString())
                .delete()
                .await()
            Timber.d("FavoritesRepository: deleted char $characterId from Firestore")
        }.onFailure { e ->
            Timber.w(e, "FavoritesRepository: Firestore delete failed for char $characterId")
        }
    }
}
