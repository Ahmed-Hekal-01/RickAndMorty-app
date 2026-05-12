package com.example.rickandmortyapp.feature.characterdetail

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the character detail screen.
 *
 * @param character the loaded character, or null while loading / on error.
 * @param isLoading true while the network request is in flight.
 * @param error user-facing error message, or null.
 */
data class CharacterDetailState(
    val character: Character? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) : UiState
