package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.remote.dto.CharacterDto
import com.example.rickandmortyapp.data.remote.dto.CharacterPageResponse
import com.example.rickandmortyapp.data.remote.dto.EpisodeDto
import com.example.rickandmortyapp.data.remote.dto.EpisodePageResponse
import com.example.rickandmortyapp.data.remote.dto.Info
import com.example.rickandmortyapp.data.remote.dto.LocationDto
import com.example.rickandmortyapp.data.remote.dto.LocationPageResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterRepositoryTest {

    @Test
    fun getAllCharacters_mapsPageToDomain() = runBlocking {
        val service = FakeApiService().apply {
            allCharactersResponse = NetworkResult.Success(
                CharacterPageResponse(
                    info = Info(count = 1, pages = 1, next = null, prev = null),
                    results = listOf(characterDto(id = 1, name = "Rick", status = "Alive"))
                )
            )
        }
        val repository = CharacterRepository(service)

        val result = repository.getAllCharacters()

        assertTrue(result is NetworkResult.Success)
        val page = (result as NetworkResult.Success).data
        assertEquals(1, page.count)
        assertEquals("Rick", page.results.first().name)
        assertEquals(CharacterStatus.ALIVE, page.results.first().status)
    }

    @Test
    fun getCharacterByPage_forwardsPageAndMapsResult() = runBlocking {
        val service = FakeApiService().apply {
            characterByPageResponse = NetworkResult.Success(
                CharacterPageResponse(
                    info = Info(count = 1, pages = 5, next = "next", prev = null),
                    results = listOf(characterDto(id = 2, name = "Morty", status = "Dead"))
                )
            )
        }
        val repository = CharacterRepository(service)

        val result = repository.getCharacterByPage(3)

        assertEquals(3, service.lastCharacterPageRequest)
        assertTrue(result is NetworkResult.Success)
        val item = (result as NetworkResult.Success).data.results.first()
        assertEquals("Morty", item.name)
        assertEquals(CharacterStatus.DEAD, item.status)
    }

    @Test
    fun getListOfCharactersByIds_forwardsIdsAndMapsList() = runBlocking {
        val service = FakeApiService().apply {
            listCharactersResponse = NetworkResult.Success(
                listOf(
                    characterDto(id = 1, name = "Rick"),
                    characterDto(id = 2, name = "Morty")
                )
            )
        }
        val repository = CharacterRepository(service)

        val result = repository.getListOfCharactersByIds(listOf(1, 2))

        assertEquals(listOf(1, 2), service.lastCharacterIdsRequest)
        assertTrue(result is NetworkResult.Success)
        assertEquals(listOf("Rick", "Morty"), (result as NetworkResult.Success).data.map { it.name })
    }

    @Test
    fun getCharacterByID_propagatesErrors() = runBlocking {
        val service = FakeApiService().apply {
            characterByIdResponse = NetworkResult.Error.BackendError.NotFound
        }
        val repository = CharacterRepository(service)

        val result = repository.getCharacterByID(404)

        assertEquals(404, service.lastCharacterIdRequest)
        assertEquals(NetworkResult.Error.BackendError.NotFound, result)
    }

    @Test
    fun searchCharacters_forwardsQueryFilterAndPage_thenMapsResult() = runBlocking {
        val service = FakeApiService().apply {
            searchCharactersResponse = NetworkResult.Success(
                CharacterPageResponse(
                    info = Info(count = 1, pages = 2, next = "next", prev = null),
                    results = listOf(characterDto(id = 5, name = "Birdperson", status = "Unknown"))
                )
            )
        }
        val repository = CharacterRepository(service)

        val result = repository.searchCharacters(
            name = "bird",
            status = CharacterStatus.UNKNOWN,
            page = 2
        )

        assertEquals("bird", service.lastSearchName)
        assertEquals("unknown", service.lastSearchStatus)
        assertEquals(2, service.lastSearchPage)
        assertTrue(result is NetworkResult.Success)
        val item = (result as NetworkResult.Success).data.results.first()
        assertEquals("Birdperson", item.name)
        assertEquals(CharacterStatus.UNKNOWN, item.status)
    }

    private class FakeApiService : IRickAndMortyApiService {
        var allCharactersResponse: NetworkResult<CharacterPageResponse>? = null
        var characterByIdResponse: NetworkResult<CharacterDto>? = null
        var characterByPageResponse: NetworkResult<CharacterPageResponse>? = null
        var listCharactersResponse: NetworkResult<List<CharacterDto>>? = null
        var searchCharactersResponse: NetworkResult<CharacterPageResponse>? = null

        var lastCharacterIdRequest: Int? = null
        var lastCharacterPageRequest: Int? = null
        var lastCharacterIdsRequest: List<Int>? = null
        var lastSearchName: String? = null
        var lastSearchStatus: String? = null
        var lastSearchPage: Int? = null

        override suspend fun getAllCharacters(): NetworkResult<CharacterPageResponse> {
            return allCharactersResponse ?: unsupported()
        }

        override suspend fun getCharacterByID(id: Int): NetworkResult<CharacterDto> {
            lastCharacterIdRequest = id
            return characterByIdResponse ?: unsupported()
        }

        override suspend fun getCharacterByPage(page: Int): NetworkResult<CharacterPageResponse> {
            lastCharacterPageRequest = page
            return characterByPageResponse ?: unsupported()
        }

        override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<CharacterDto>> {
            lastCharacterIdsRequest = ids
            return listCharactersResponse ?: unsupported()
        }

        override suspend fun searchCharacters(
            name: String,
            status: String?,
            page: Int
        ): NetworkResult<CharacterPageResponse> {
            lastSearchName = name
            lastSearchStatus = status
            lastSearchPage = page
            return searchCharactersResponse ?: unsupported()
        }

        override suspend fun getAllLocations(): NetworkResult<LocationPageResponse> = unsupported()
        override suspend fun getLocationByID(id: Int): NetworkResult<LocationDto> = unsupported()
        override suspend fun getLocationByPage(page: Int): NetworkResult<LocationPageResponse> = unsupported()
        override suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<LocationDto>> = unsupported()
        override suspend fun getAllEpisodes(): NetworkResult<EpisodePageResponse> = unsupported()
        override suspend fun getEpisodeByID(id: Int): NetworkResult<EpisodeDto> = unsupported()
        override suspend fun getEpisodeByPage(page: Int): NetworkResult<EpisodePageResponse> = unsupported()
        override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<EpisodeDto>> = unsupported()

        private fun <T> unsupported(): T = throw UnsupportedOperationException("Not used in this test")
    }

    private fun characterDto(
        id: Int,
        name: String,
        status: String = "Alive"
    ) = CharacterDto(
        id = id,
        name = name,
        status = status,
        species = "Human",
        type = "",
        gender = "Male",
        image = "https://example.com/$id.png",
        origin = CharacterDto.Place(name = "Earth", url = "https://example.com/origin"),
        location = CharacterDto.Place(name = "Earth", url = "https://example.com/location"),
        episode = listOf("https://rickandmortyapi.com/api/episode/1")
    )
}

