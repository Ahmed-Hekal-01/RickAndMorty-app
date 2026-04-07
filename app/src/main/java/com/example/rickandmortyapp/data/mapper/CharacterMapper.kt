package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.dto.CharacterDto
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.dto.CharacterPageResponse

fun CharacterDto.toDomain(): Character {
    return Character(
        id = this.id,
        name = this.name,
        imageUrl = this.image,
        status = when (this.status.lowercase()) {
            "alive" -> CharacterStatus.ALIVE
            "dead" -> CharacterStatus.DEAD
            else -> CharacterStatus.UNKNOWN
        },
        species = this.species,
        gender = this.gender,
        origin = this.origin.name,
        location = this.location.name,
        episodeIds = this.episode.map { url -> url.substringAfterLast("/") }
    )
}