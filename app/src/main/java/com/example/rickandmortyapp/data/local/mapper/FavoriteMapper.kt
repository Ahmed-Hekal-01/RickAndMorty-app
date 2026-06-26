package com.example.rickandmortyapp.data.local.mapper

import com.example.rickandmortyapp.data.local.entity.FavoriteCharacterEntity
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus

/**
 * Bidirectional mappers between the [Character] domain model and
 * [FavoriteCharacterEntity] Room entity.
 *
 * Keeping mapping logic here (rather than in the repository or ViewModel)
 * upholds the Single Responsibility Principle and makes the mapping trivially
 * testable without an Android context.
 */

/** Maps a domain [Character] to a [FavoriteCharacterEntity] for local persistence. */
fun Character.toFavoriteEntity(userId: String): FavoriteCharacterEntity =
    FavoriteCharacterEntity(
        characterId = id,
        userId = userId,
        name = name,
        imageUrl = imageUrl,
        species = species,
        status = status.name,           // persist enum name, re-parse on the way back out
        gender = gender,
        origin = origin,
        location = location,
        isSyncedToCloud = false         // will be updated once Firestore confirms
    )

/** Maps a [FavoriteCharacterEntity] back to the domain [Character] model. */
fun FavoriteCharacterEntity.toCharacter(): Character =
    Character(
        id = characterId,
        name = name,
        imageUrl = imageUrl,
        status = runCatching { CharacterStatus.valueOf(status) }
            .getOrDefault(CharacterStatus.UNKNOWN),
        species = species,
        gender = gender,
        origin = origin,
        location = location,
        episodeIds = emptyList()        // episode IDs are not cached in the favorites table
    )
