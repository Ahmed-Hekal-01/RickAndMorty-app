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

class LocationRepositoryTest {

    @Test
    fun getAllLocations_mapsPageAndResidentIds() = runBlocking {
        val service = FakeApiService().apply {
            allLocationsResponse = NetworkResult.Success(
                LocationPageResponse(
                    info = Info(count = 1, pages = 1, next = null, prev = null),
                    results = listOf(
                        locationDto(
                            id = 7,
                            name = "Earth",
                            residents = listOf(
                                "https://rickandmortyapi.com/api/character/1",
                                "https://rickandmortyapi.com/api/character/2"
                            )
                        )
                    )
                )
            )
        }
        val repository = LocationRepository(service)

        val result = repository.getAllLocations()

        assertTrue(result is NetworkResult.Success)
        val item = (result as NetworkResult.Success).data.results.first()
        assertEquals("Earth", item.name)
        assertEquals(listOf("1", "2"), item.residentCharactersIds)
    }

    @Test
    fun getLocationByPage_forwardsPageParameter() = runBlocking {
        val service = FakeApiService().apply {
            locationByPageResponse = NetworkResult.Success(
                LocationPageResponse(
                    info = Info(count = 1, pages = 3, next = "next", prev = null),
                    results = listOf(locationDto(id = 9, name = "Citadel", residents = emptyList()))
                )
            )
        }
        val repository = LocationRepository(service)

        val result = repository.getLocationByPage(2)

        assertEquals(2, service.lastLocationPageRequest)
        assertTrue(result is NetworkResult.Success)
    }

    @Test
    fun getLocationByID_propagatesErrors() = runBlocking {
        val service = FakeApiService().apply {
            locationByIdResponse = NetworkResult.Error.BackendError.Unavailable
        }
        val repository = LocationRepository(service)

        val result = repository.getLocationByID(503)

        assertEquals(503, service.lastLocationIdRequest)
        assertEquals(NetworkResult.Error.BackendError.Unavailable, result)
    }

    private class FakeApiService : IRickAndMortyApiService {
        var allLocationsResponse: NetworkResult<LocationPageResponse>? = null
        var locationByIdResponse: NetworkResult<LocationDto>? = null
        var locationByPageResponse: NetworkResult<LocationPageResponse>? = null

        var lastLocationIdRequest: Int? = null
        var lastLocationPageRequest: Int? = null

        override suspend fun getAllLocations(): NetworkResult<LocationPageResponse> {
            return allLocationsResponse ?: unsupported()
        }

        override suspend fun getLocationByID(id: Int): NetworkResult<LocationDto> {
            lastLocationIdRequest = id
            return locationByIdResponse ?: unsupported()
        }

        override suspend fun getLocationByPage(page: Int): NetworkResult<LocationPageResponse> {
            lastLocationPageRequest = page
            return locationByPageResponse ?: unsupported()
        }

        override suspend fun getCharacterByID(id: Int): NetworkResult<CharacterDto> = unsupported()
        override suspend fun getCharacterByPage(page: Int): NetworkResult<CharacterPageResponse> = unsupported()
        override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<CharacterDto>> = unsupported()
        override suspend fun getAllCharacters(): NetworkResult<CharacterPageResponse> = unsupported()
        override suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<LocationDto>> = unsupported()
        override suspend fun getAllEpisodes(): NetworkResult<EpisodePageResponse> = unsupported()
        override suspend fun getEpisodeByID(id: Int): NetworkResult<EpisodeDto> = unsupported()
        override suspend fun getEpisodeByPage(page: Int): NetworkResult<EpisodePageResponse> = unsupported()
        override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<EpisodeDto>> = unsupported()

        private fun <T> unsupported(): T = throw UnsupportedOperationException("Not used in this test")
    }

    private fun locationDto(
        id: Int,
        name: String,
        residents: List<String>
    ) = LocationDto(
        id = id,
        name = name,
        type = "Planet",
        dimension = "Dimension C-137",
        residentCharacters = residents
    )
}

