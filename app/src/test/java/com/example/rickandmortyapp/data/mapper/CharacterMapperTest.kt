package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.dto.CharacterDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterMapperTest {

    @Test
    fun toDomain_mapsAliveStatusCaseInsensitively() {
        val dto = characterDto(status = "aLiVe", episode = listOf("https://rickandmortyapi.com/api/episode/10"))

        val result = dto.toDomain()

        assertEquals(CharacterStatus.ALIVE, result.status)
        assertEquals(listOf("10"), result.episodeIds)
    }

    @Test
    fun toDomain_mapsDeadStatus() {
        val dto = characterDto(status = "Dead")

        val result = dto.toDomain()

        assertEquals(CharacterStatus.DEAD, result.status)
    }

    @Test
    fun toDomain_mapsUnknownForUnexpectedStatus() {
        val dto = characterDto(status = "Ghost")

        val result = dto.toDomain()

        assertEquals(CharacterStatus.UNKNOWN, result.status)
    }

    @Test
    fun toDomain_mapsEpisodeUrlsToIds() {
        val dto = characterDto(
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/28"
            )
        )

        val result = dto.toDomain()

        assertEquals(listOf("1", "28"), result.episodeIds)
    }

    private fun characterDto(
        status: String = "Alive",
        episode: List<String> = emptyList()
    ) = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = status,
        species = "Human",
        type = "",
        gender = "Male",
        image = "https://example.com/1.png",
        origin = CharacterDto.Place(name = "Earth", url = "https://example.com/origin"),
        location = CharacterDto.Place(name = "Citadel", url = "https://example.com/location"),
        episode = episode
    )
}

