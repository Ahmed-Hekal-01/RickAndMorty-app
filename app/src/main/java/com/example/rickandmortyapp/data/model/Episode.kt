package com.example.rickandmortyapp.data.model

data class Episode (

    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeNumber : Int,
    val airDate: String,
    val characterIds: List<String>
)
