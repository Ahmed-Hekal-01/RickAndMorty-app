package com.example.rickandmortyapp.feature.home

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the home (character list) screen.
 *
 * @param characters    accumulated list of all characters fetched across pages.
 * @param favoriteIds   live set of character IDs the current user has favourited.
 *                      Backed by Room — updated automatically on any toggle.
 *                      O(1) lookup in [CharacterCard] via [Set.contains].
 * @param isLoading     true during the initial page-1 load.
 * @param isLoadingMore true while fetching a subsequent page (pagination spinner).
 * @param currentPage   the last successfully loaded page number.
 * @param hasMorePages  false when the API returns null for 'next' — stops pagination.
 * @param error         user-facing error message, or null if everything is fine.
 */
data class HomeState(
    val characters: List<Character> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null
) : UiState
