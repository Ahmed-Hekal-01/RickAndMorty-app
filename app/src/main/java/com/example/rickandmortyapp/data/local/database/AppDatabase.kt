package com.example.rickandmortyapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rickandmortyapp.data.local.dao.FavoriteCharacterDao
import com.example.rickandmortyapp.data.local.dao.CharacterCacheDao
import com.example.rickandmortyapp.data.local.entity.FavoriteCharacterEntity
import com.example.rickandmortyapp.data.local.entity.CachedCharacterEntity

/**
 * Single Room database for the app.
 *
 * **Version history**
 * | Version | Change                       |
 * |---------|------------------------------|
 * | 1       | Initial schema: favorites    |
 *
 * To add a new entity in the future:
 * 1. Increase [version] by 1.
 * 2. Add the entity to [entities].
 * 3. Write and register a [androidx.room.migration.Migration].
 *
 * [exportSchema] is `true` so Room writes the schema JSON to `app/schemas/`.
 * This file should be committed to version control so schema diffs are reviewable.
 */
@Database(
    entities = [
        FavoriteCharacterEntity::class,
        CachedCharacterEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCharacterDao(): FavoriteCharacterDao
    abstract fun characterCacheDao(): CharacterCacheDao
}
