package com.example.rickandmortyapp.data.api.service

import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.remote.dto.CharacterDto
import com.example.rickandmortyapp.data.remote.dto.CharacterPageResponse
import com.example.rickandmortyapp.data.remote.dto.EpisodeDto
import com.example.rickandmortyapp.data.remote.dto.EpisodePageResponse
import com.example.rickandmortyapp.data.remote.dto.LocationDto
import com.example.rickandmortyapp.data.remote.dto.LocationPageResponse

interface IRickAndMortyApiService {

    suspend fun getAllCharacters(): NetworkResult<CharacterPageResponse>
    suspend fun getCharacterByID(id : Int): NetworkResult<CharacterDto>
    suspend fun getCharacterByPage(page : Int) : NetworkResult<CharacterPageResponse>
    suspend fun getListOfCharactersByIds(ids : List<Int>) : NetworkResult<List<CharacterDto>>
    suspend fun searchCharacters(
        name: String,
        status: String? = null,
        page: Int = 1
    ): NetworkResult<CharacterPageResponse>

    suspend fun getAllLocations(): NetworkResult<LocationPageResponse>
    suspend fun getLocationByID(id : Int): NetworkResult<LocationDto>
    suspend fun getLocationByPage(page : Int) : NetworkResult<LocationPageResponse>
    suspend fun getListOfLocationsByIds(ids : List<Int>) : NetworkResult<List<LocationDto>>

    suspend fun getAllEpisodes(): NetworkResult<EpisodePageResponse>
    suspend fun getEpisodeByID(id : Int): NetworkResult<EpisodeDto>
    suspend fun getEpisodeByPage(page : Int) : NetworkResult<EpisodePageResponse>
    suspend fun getListOfEpisodesByIds(ids : List<Int>) : NetworkResult<List<EpisodeDto>>
}