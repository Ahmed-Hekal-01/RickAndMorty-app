package com.example.rickandmortyapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Room entity representing a character the user has marked as a favourite.
 *
 * **Composite PK `(characterId, userId)`:**
 * Since the app requires sign-in, [userId] is always a real Firebase UID.
 * The composite key allows a single local DB to correctly handle account
 * switching without schema migrations.
 *
 * @param characterId       The Rick & Morty API character ID.
 * @param userId            Firebase UID of the owning user. Never null or anonymous
 *                          because the app requires sign-in.
 * @param name              Cached display name.
 * @param imageUrl          Cached avatar URL.
 * @param species           Cached species string.
 * @param status            Cached alive/dead/unknown status string.
 * @param gender            Cached gender string.
 * @param origin            Cached origin location name.
 * @param location          Cached current location name.
 * @param addedAt           Unix epoch milliseconds when the character was favourited.
 * @param isSyncedToCloud   `true` once Firestore acknowledges the write,
 *                          or `true` when the row was fetched FROM Firestore.
 */
@Entity(
    tableName = "favorite_characters",
    primaryKeys = ["characterId", "userId"],
    indices = [Index(value = ["userId"])]
)
data class FavoriteCharacterEntity(
    val characterId: Int,
    val userId: String,
    val name: String,
    val imageUrl: String,
    val species: String,
    val status: String,
    val gender: String,
    val origin: String,
    val location: String,
    val addedAt: Long = System.currentTimeMillis(),
    val isSyncedToCloud: Boolean = false
)
