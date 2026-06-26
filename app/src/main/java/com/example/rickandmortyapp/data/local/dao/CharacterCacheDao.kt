package com.example.rickandmortyapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortyapp.data.local.entity.CachedCharacterEntity

@Dao
interface CharacterCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CachedCharacterEntity>)

    @Query("SELECT * FROM cached_characters WHERE page = :page ORDER BY id ASC")
    suspend fun getCharactersByPage(page: Int): List<CachedCharacterEntity>
    
    @Query("DELETE FROM cached_characters")
    suspend fun clearAll()
}
