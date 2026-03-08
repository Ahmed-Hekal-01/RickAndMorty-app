package com.example.rickandmortyapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String,
    val origin: Place,
    val location: Place,
    val episode: List<String>

) {
    @Serializable
    data class Place(
        val name: String,
        val url: String
    )
}

@Serializable
data class CharacterPageResponse(
    override val info: Info,
    override val result: List<CharacterDto>
) : IPageResponse<CharacterDto>