package com.example.rickandmortyapp.feature.search

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the character search screen.
 *
 * @param query current text field value.
 * @param statusFilter active status filter, or null for no filter.
 * @param results accumulated search results across pages.
 * @param isLoading true during the initial search request (page 1).
 * @param isLoadingMore true while fetching additional pages.
 * @param currentPage last successfully loaded page number.
 * @param hasMorePages false when the API 'next' field is null.
 * @param error user-facing error message, or null.
 * @param hasSearched true once the user has performed at least one search.
 */
data class SearchState(
    val query: String = "",
    val statusFilter: CharacterStatus? = null,
    val results: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val hasSearched: Boolean = false
) : UiState
