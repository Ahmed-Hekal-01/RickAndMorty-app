package com.example.rickandmortyapp.data.api.service

import com.example.rickandmortyapp.BuildConfig
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.remote.dto.CharacterDto
import com.example.rickandmortyapp.data.remote.dto.CharacterPageResponse
import com.example.rickandmortyapp.data.remote.dto.EpisodeDto
import com.example.rickandmortyapp.data.remote.dto.EpisodePageResponse
import com.example.rickandmortyapp.data.remote.dto.LocationDto
import com.example.rickandmortyapp.data.remote.dto.LocationPageResponse
import com.example.rickandmortyapp.data.remote.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class KtorRickAndMortyService @Inject constructor(
    private val client : HttpClient
) : IRickAndMortyApiService{

    private val baseUrl = BuildConfig.BASE_URL
    override suspend fun getAllCharacters(): NetworkResult<CharacterPageResponse> {
        return safeApiCall {
            client.get("$baseUrl/character").body()
        }
    }

    override suspend fun getCharacterByID(id: Int): NetworkResult<CharacterDto> {
        return safeApiCall {
            client.get("$baseUrl/character/$id").body()
        }
    }

    override suspend fun getCharacterByPage(page: Int): NetworkResult<CharacterPageResponse> {
        return safeApiCall {
            client.get("$baseUrl/character") {
                parameter("page" , page)
            }.body()
        }
    }

    override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<CharacterDto>> {
        return safeApiCall {
            val idsString = ids.joinToString(",")
            client.get("$baseUrl/character/$idsString").body()
        }
    }

    override suspend fun getAllLocations(): NetworkResult<LocationPageResponse> {
        return safeApiCall {
            client.get("$baseUrl/location").body()
        }
    }

    override suspend fun getLocationByID(id: Int): NetworkResult<LocationDto> {
        return safeApiCall {
            client.get("$baseUrl/location/$id").body()
        }
    }

    override suspend fun getLocationByPage(page: Int): NetworkResult<LocationPageResponse> {
        return safeApiCall {
            client.get("$baseUrl/location"){
                parameter("page" , page)
            }.body()
        }
    }

    override suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<LocationDto>> {
        return safeApiCall {
            val idsString = ids.joinToString(",")
            client.get("$baseUrl/location/$idsString").body()
        }
    }

    override suspend fun getAllEpisodes(): NetworkResult<EpisodePageResponse> {
        return safeApiCall {
            client.get("$baseUrl/episode").body()
        }
    }

    override suspend fun getEpisodeByID(id: Int): NetworkResult<EpisodeDto> {
        return safeApiCall {
            client.get("$baseUrl/episode/$id").body()
        }
    }

    override suspend fun getEpisodeByPage(page: Int): NetworkResult<EpisodePageResponse> {
        return safeApiCall {
            client.get("$baseUrl/episode") {
                parameter("page" , page)
            }.body()
        }
    }

    override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<EpisodeDto>> {
        return safeApiCall {
            val idsString = ids.joinToString(",")
            client.get("$baseUrl/episode/$idsString").body()
        }
    }

}