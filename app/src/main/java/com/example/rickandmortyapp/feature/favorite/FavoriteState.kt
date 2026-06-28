package com.example.rickandmortyapp.feature.favorite

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.feature.base.UiState

/**
 * Immutable UI state for the Favorites screen.
 *
 * @param favorites   The current user's favourite characters, newest-first.
 * @param syncMessage A transient message shown while cloud sync is in progress
 *                    (e.g. "Syncing…"). Null when idle.
 */
data class FavoriteState(
    val favorites: List<Character> = emptyList(),
    val syncMessage: String? = null
) : UiState
