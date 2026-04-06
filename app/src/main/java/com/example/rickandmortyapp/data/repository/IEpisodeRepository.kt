package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult

interface IEpisodeRepository {
    suspend fun getAllEpisodes(): NetworkResult<Page<Episode>>
    suspend fun getEpisodeByID(id: Int): NetworkResult<Episode>
    suspend fun getEpisodeByPage(page: Int): NetworkResult<Page<Episode>>
    suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<Episode>>
}
