package com.example.rickandmortyapp.data.repository


import com.example.rickandmortyapp.data.api.service.IRickAndMortyApiService
import com.example.rickandmortyapp.data.mapper.mapSuccess
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.data.mapper.toPage
import com.example.rickandmortyapp.data.model.Location
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: IRickAndMortyApiService
) : ILocationRepository {

    override suspend fun getAllLocations(): NetworkResult<Page<Location>> {
        return apiService.getAllLocations().mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getLocationByID(id: Int): NetworkResult<Location> {
        return apiService.getLocationByID(id).mapSuccess { it.toDomain() }
    }

    override suspend fun getLocationByPage(page: Int): NetworkResult<Page<Location>> {
        return apiService.getLocationByPage(page).mapSuccess { dtoPage ->
            dtoPage.toPage { it.toDomain() }
        }
    }

    override suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<Location>> {
        return apiService.getListOfLocationsByIds(ids).mapSuccess { list ->
            list.map { it.toDomain() }
        }
    }
}
