package com.example.rickandmortyapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationPageResponse(
    override val info: Info,
    override val results: List<LocationDto>
) : IPageResponse<LocationDto>

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    @SerialName("residents")
    val residentCharacters: List<String>
)