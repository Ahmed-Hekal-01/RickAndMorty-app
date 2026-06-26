package com.example.rickandmortyapp.data.repository


import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.mapper.mapSuccess
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.data.mapper.toPage
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val apiService: IRickAndMortyApiService
) : IEpisodeRepository {

    override suspend fun getAllEpisodes(): NetworkResult<Page<Episode>> {
        return apiService.getAllEpisodes().mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getEpisodeByID(id: Int): NetworkResult<Episode> {
        return apiService.getEpisodeByID(id).mapSuccess { it.toDomain() }
    }

    override suspend fun getEpisodeByPage(page: Int): NetworkResult<Page<Episode>> {
        return apiService.getEpisodeByPage(page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<Episode>> {
        if (ids.isEmpty()) return NetworkResult.Success(emptyList())
        if (ids.size == 1) {
            return getEpisodeByID(ids[0]).mapSuccess { listOf(it) }
        }
        return apiService.getListOfEpisodesByIds(ids).mapSuccess { list ->
            list.map { it.toDomain() }
        }
    }
}
