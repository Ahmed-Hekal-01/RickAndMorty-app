package com.example.rickandmortyapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Caches a character fetched from the API for the paginated Home screen.
 * This prevents re-fetching the same pages on configuration changes or back navigation,
 * which helps avoid 429 Too Many Requests errors.
 */
@Entity(tableName = "cached_characters")
data class CachedCharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val page: Int
)
