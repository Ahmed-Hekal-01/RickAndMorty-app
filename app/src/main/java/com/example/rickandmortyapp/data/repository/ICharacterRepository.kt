package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult

interface ICharacterRepository {
    suspend fun getAllCharacters(): NetworkResult<Page<Character>>
    suspend fun getCharacterByID(id: Int): NetworkResult<Character>
    suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>>
    suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<Character>>
}
