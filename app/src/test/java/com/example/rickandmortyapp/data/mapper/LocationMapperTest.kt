package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.remote.dto.LocationDto
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationMapperTest {

    @Test
    fun toDomain_extractsResidentIdsFromUrls() {
        val dto = locationDto(
            residents = listOf(
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/22"
            )
        )

        val result = dto.toDomain()

        assertEquals(listOf("1", "22"), result.residentCharactersIds)
    }

    @Test
    fun toDomain_keepsEmptyResidentsAsEmptyIds() {
        val dto = locationDto(residents = emptyList())

        val result = dto.toDomain()

        assertEquals(emptyList<String>(), result.residentCharactersIds)
    }

    private fun locationDto(residents: List<String>) = LocationDto(
        id = 3,
        name = "Anatomy Park",
        type = "Microverse",
        dimension = "Dimension C-137",
        residentCharacters = residents
    )
}

