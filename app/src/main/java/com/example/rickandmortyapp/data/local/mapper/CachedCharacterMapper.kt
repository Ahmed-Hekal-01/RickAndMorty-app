package com.example.rickandmortyapp.data.local.mapper

import com.example.rickandmortyapp.data.local.entity.CachedCharacterEntity
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus

fun Character.toCachedEntity(page: Int): CachedCharacterEntity =
    CachedCharacterEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        status = status.name,
        species = species,
        gender = gender,
        origin = origin,
        location = location,
        page = page
    )

fun CachedCharacterEntity.toCharacter(): Character =
    Character(
        id = id,
        name = name,
        imageUrl = imageUrl,
        status = runCatching { CharacterStatus.valueOf(status) }.getOrDefault(CharacterStatus.UNKNOWN),
        species = species,
        gender = gender,
        origin = origin,
        location = location,
        episodeIds = emptyList() // Not stored in cache to save space, not needed for Home
    )
