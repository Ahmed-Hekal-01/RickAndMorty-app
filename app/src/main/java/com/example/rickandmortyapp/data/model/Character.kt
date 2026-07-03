package com.example.rickandmortyapp.data.model

import androidx.compose.ui.graphics.Color

data class Character(
        val id: Int,
        val name: String,
        val imageUrl: String,
        val status: CharacterStatus,
        val species: String,
        val gender: String,
        val origin: String,
        val location: String,
        val episodeIds: List<String>
)

enum class CharacterStatus(val displayName: String) {
    ALIVE("Alive"),
    DEAD("Dead"),
    UNKNOWN("Unknown")
}

val CharacterStatus.displayNameRes: Int
    get() = when(this){
        CharacterStatus.ALIVE -> com.example.rickandmortyapp.R.string.status_alive
        CharacterStatus.DEAD -> com.example.rickandmortyapp.R.string.status_dead
        CharacterStatus.UNKNOWN -> com.example.rickandmortyapp.R.string.status_unknown
    }

val CharacterStatus.color : Color
    get() = when(this){
        CharacterStatus.ALIVE -> Color.Green
        CharacterStatus.DEAD -> Color.Red
        CharacterStatus.UNKNOWN -> Color.Yellow
    }
