package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.mapper.mapSuccess
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.data.mapper.toPage
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val apiService: IRickAndMortyApiService
) : ICharacterRepository {
    override suspend fun getAllCharacters(): NetworkResult<Page<Character>> {
        return apiService.getAllCharacters().mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getCharacterByID(id: Int): NetworkResult<Character> {
        return apiService.getCharacterByID(id).mapSuccess { it.toDomain() }
    }

    override suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>> {
        return apiService.getCharacterByPage(page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<Character>> {
        return apiService.getListOfCharactersByIds(ids).mapSuccess { idsList ->
            idsList.map { it.toDomain() }
        }
    }

    override suspend fun searchCharacters(
        name: String,
        status: CharacterStatus?,
        page: Int
    ): NetworkResult<Page<Character>> {
        val statusString = status?.name?.lowercase()
        return apiService.searchCharacters(name, statusString, page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }
}