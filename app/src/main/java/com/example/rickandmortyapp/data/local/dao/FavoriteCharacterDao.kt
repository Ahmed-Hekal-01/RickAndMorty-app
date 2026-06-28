package com.example.rickandmortyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapp.data.local.entity.FavoriteCharacterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [FavoriteCharacterEntity].
 *
 * All read operations return [Flow] so Room automatically emits a new list
 * whenever the underlying table changes. This is the cornerstone of the
 * Single Source of Truth: every ViewModel observing [observeFavorites] will
 * receive identical, live-updated lists without any additional synchronisation.
 */
@Dao
interface FavoriteCharacterDao {

    /**
     * Emits the full favourite list for [userId] every time the table changes.
     * Ordered newest-first so the most recently added characters appear at the top.
     */
    @Query(
        """
        SELECT * FROM favorite_characters
        WHERE userId = :userId
        ORDER BY addedAt DESC
        """
    )
    fun observeFavorites(userId: String): Flow<List<FavoriteCharacterEntity>>

    /**
     * Returns only the set of favourite character IDs for [userId].
     * Used by HomeViewModel to compute `isFavorite` for each card without
     * loading full entity data.
     *
     * Note: returns [List] because Room KSP cannot map query results directly
     * to [Set]. The conversion to [Set] is done in [FavoritesRepository].
     */
    @Query(
        """
        SELECT characterId FROM favorite_characters
        WHERE userId = :userId
        """
    )
    fun observeFavoriteIds(userId: String): Flow<List<Int>>

    /**
     * Emits `true` when the character is in the user's favourite list.
     * Consumed by CharacterDetailViewModel for the heart toggle.
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM favorite_characters
        WHERE characterId = :characterId AND userId = :userId
        """
    )
    fun observeIsFavorite(characterId: Int, userId: String): Flow<Boolean>

    /**
     * Upsert semantics: re-favouriting an already-favourite character
     * (e.g., after a userId transition) simply refreshes the row.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(entity: FavoriteCharacterEntity)

    /** Hard delete — called when the user un-hearts a character. */
    @Query(
        """
        DELETE FROM favorite_characters
        WHERE characterId = :characterId AND userId = :userId
        """
    )
    suspend fun deleteFavorite(characterId: Int, userId: String)

    /**
     * Returns all rows that have not yet been confirmed by Firestore.
     * Used by the sync worker to retry pending cloud writes after
     * connectivity is restored.
     */
    @Query(
        """
        SELECT * FROM favorite_characters
        WHERE userId = :userId AND isSyncedToCloud = 0
        """
    )
    suspend fun getPendingSyncFavorites(userId: String): List<FavoriteCharacterEntity>

    /**
     * Marks a favourite as successfully synced to Firestore.
     * Called from [FavoritesRepository] once the cloud write is confirmed.
     */
    @Query(
        """
        UPDATE favorite_characters
        SET isSyncedToCloud = 1
        WHERE characterId = :characterId AND userId = :userId
        """
    )
    suspend fun markAsSynced(characterId: Int, userId: String)
}
