package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.remote.dto.EpisodeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class EpisodeMapperTest {

    @Test
    fun toDomain_parsesSeasonAndEpisodeFromCode() {
        val dto = episodeDto(episodeCode = "S02E05")

        val result = dto.toDomain()

        assertEquals(2, result.seasonNumber)
        assertEquals(5, result.episodeNumber)
    }

    @Test
    fun toDomain_returnsZeroesWhenEpisodeCodeInvalid() {
        val dto = episodeDto(episodeCode = "INVALID")

        val result = dto.toDomain()

        assertEquals(0, result.seasonNumber)
        assertEquals(0, result.episodeNumber)
    }

    @Test
    fun toDomain_extractsCharacterIdsFromUrls() {
        val dto = episodeDto(
            episodeCode = "S01E01",
            characters = listOf(
                "https://rickandmortyapi.com/api/character/10",
                "https://rickandmortyapi.com/api/character/35"
            )
        )

        val result = dto.toDomain()

        assertEquals(listOf("10", "35"), result.characterIds)
    }

    private fun episodeDto(
        episodeCode: String,
        characters: List<String> = emptyList()
    ) = EpisodeDto(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = episodeCode,
        characters = characters
    )
}

