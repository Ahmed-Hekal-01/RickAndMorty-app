package com.example.rickandmortyapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String,
    @SerialName("air_date")
    val airDate: String,
    val episode: String, //"S01E01"
    val characters: List<String>
)

@Serializable
data class EpisodePageResponse(
    override val results: List<EpisodeDto>,
    override val info: Info
) : IPageResponse<EpisodeDto>