package com.example.rickandmortyapp.feature.search

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the character search screen.
 *
 * Search supports two inputs only:
 * 1. Character name [query].
 * 2. Character status [statusFilter]: Alive / Dead / Unknown.
 */
data class SearchState(
    val query: String = "",
    val statusFilter: CharacterStatus? = null,
    val results: List<Character> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val hasSearched: Boolean = false
) : UiState
