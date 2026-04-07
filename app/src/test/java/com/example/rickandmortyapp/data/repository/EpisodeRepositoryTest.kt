package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
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

class EpisodeRepositoryTest {

    @Test
    fun getAllEpisodes_mapsPageAndSeasonEpisode() = runBlocking {
        val service = FakeApiService().apply {
            allEpisodesResponse = NetworkResult.Success(
                EpisodePageResponse(
                    results = listOf(episodeDto(id = 1, name = "Pilot", code = "S01E03")),
                    info = Info(count = 1, pages = 1, next = null, prev = null)
                )
            )
        }
        val repository = EpisodeRepository(service)

        val result = repository.getAllEpisodes()

        assertTrue(result is NetworkResult.Success)
        val item = (result as NetworkResult.Success).data.results.first()
        assertEquals("Pilot", item.name)
        assertEquals(1, item.seasonNumber)
        assertEquals(3, item.episodeNumber)
    }

    @Test
    fun getEpisodeByPage_forwardsPageParameter() = runBlocking {
        val service = FakeApiService().apply {
            episodeByPageResponse = NetworkResult.Success(
                EpisodePageResponse(
                    results = listOf(episodeDto(id = 10, name = "Close Rick-counters", code = "S01E10")),
                    info = Info(count = 1, pages = 3, next = "next", prev = null)
                )
            )
        }
        val repository = EpisodeRepository(service)

        val result = repository.getEpisodeByPage(3)

        assertEquals(3, service.lastEpisodePageRequest)
        assertTrue(result is NetworkResult.Success)
    }

    @Test
    fun getEpisodeByID_propagatesErrors() = runBlocking {
        val service = FakeApiService().apply {
            episodeByIdResponse = NetworkResult.Error.BackendError.NotFound
        }
        val repository = EpisodeRepository(service)

        val result = repository.getEpisodeByID(404)

        assertEquals(404, service.lastEpisodeIdRequest)
        assertEquals(NetworkResult.Error.BackendError.NotFound, result)
    }

    private class FakeApiService : IRickAndMortyApiService {
        var allEpisodesResponse: NetworkResult<EpisodePageResponse>? = null
        var episodeByIdResponse: NetworkResult<EpisodeDto>? = null
        var episodeByPageResponse: NetworkResult<EpisodePageResponse>? = null

        var lastEpisodeIdRequest: Int? = null
        var lastEpisodePageRequest: Int? = null

        override suspend fun getAllEpisodes(): NetworkResult<EpisodePageResponse> {
            return allEpisodesResponse ?: unsupported()
        }

        override suspend fun getEpisodeByID(id: Int): NetworkResult<EpisodeDto> {
            lastEpisodeIdRequest = id
            return episodeByIdResponse ?: unsupported()
        }

        override suspend fun getEpisodeByPage(page: Int): NetworkResult<EpisodePageResponse> {
            lastEpisodePageRequest = page
            return episodeByPageResponse ?: unsupported()
        }

        override suspend fun getCharacterByID(id: Int): NetworkResult<CharacterDto> = unsupported()
        override suspend fun getCharacterByPage(page: Int): NetworkResult<CharacterPageResponse> = unsupported()
        override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<CharacterDto>> = unsupported()
        override suspend fun getAllCharacters(): NetworkResult<CharacterPageResponse> = unsupported()
        override suspend fun getAllLocations(): NetworkResult<LocationPageResponse> = unsupported()
        override suspend fun getLocationByID(id: Int): NetworkResult<LocationDto> = unsupported()
        override suspend fun getLocationByPage(page: Int): NetworkResult<LocationPageResponse> = unsupported()
        override suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<LocationDto>> = unsupported()
        override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<EpisodeDto>> = unsupported()

        private fun <T> unsupported(): T = throw UnsupportedOperationException("Not used in this test")
    }

    private fun episodeDto(
        id: Int,
        name: String,
        code: String
    ) = EpisodeDto(
        id = id,
        name = name,
        airDate = "December 2, 2013",
        episode = code,
        characters = listOf("https://rickandmortyapi.com/api/character/1")
    )
}

