package com.example.rickandmortyapp.feature.episodes

import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the episodes list screen.
 *
 * @param episodes accumulated list of all episodes fetched across pages.
 * @param isLoading true during the initial page-1 load.
 * @param isLoadingMore true while fetching a subsequent page.
 * @param currentPage last successfully loaded page number.
 * @param hasMorePages false when the API 'next' field is null.
 * @param error user-facing error message, or null.
 */
data class EpisodesState(
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val error: String? = null
) : UiState
