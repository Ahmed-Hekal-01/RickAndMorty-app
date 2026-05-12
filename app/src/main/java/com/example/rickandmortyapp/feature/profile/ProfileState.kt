package com.example.rickandmortyapp.feature.profile

import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.feature.base.UiState

/**
 * UI state for the user profile screen.
 *
 * @param profile the loaded [UserProfile], or null while loading / on error.
 * @param isLoading true during the initial profile fetch.
 * @param isSaving true while a profile update is being saved.
 * @param error user-facing error message, or null.
 */
data class ProfileState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
) : UiState
