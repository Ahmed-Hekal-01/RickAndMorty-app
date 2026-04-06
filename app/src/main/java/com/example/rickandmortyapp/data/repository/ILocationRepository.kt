package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.model.Location
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult

interface ILocationRepository {
    suspend fun getAllLocations(): NetworkResult<Page<Location>>
    suspend fun getLocationByID(id: Int): NetworkResult<Location>
    suspend fun getLocationByPage(page: Int): NetworkResult<Page<Location>>
    suspend fun getListOfLocationsByIds(ids: List<Int>): NetworkResult<List<Location>>
}
