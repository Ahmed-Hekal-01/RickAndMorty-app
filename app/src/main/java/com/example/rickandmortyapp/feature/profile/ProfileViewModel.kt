package com.example.rickandmortyapp.feature.profile

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.data.repository.IUserProfileRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages user profile screen state.
 *
 * - Loads the Firebase user profile on init.
 * - Supports updating the display name via [IUserProfileRepository].
 * - Logout clears both the Firebase session ([IAuthRepository]) and
 *   the persisted token ([ISessionRepository]) before navigating to login.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: IUserProfileRepository,
    private val authRepository: IAuthRepository,
    private val sessionRepository: ISessionRepository
) : MviViewModel<ProfileState, ProfileEvent, ProfileEffect>() {

    override fun createInitialState() = ProfileState()

    init {
        onEvent(ProfileEvent.LoadProfile)
    }

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.UpdateDisplayName -> updateDisplayName(event.name)
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = userProfileRepository.getCurrentProfile()) {
                is NetworkResult.Success -> {
                    setState { copy(profile = result.data, isLoading = false) }
                }
                is NetworkResult.Error -> {
                    val message = "Failed to load profile."
                    setState { copy(isLoading = false, error = message) }
                    setEffect(ProfileEffect.ShowError(message))
                }
            }
        }
    }

    private fun updateDisplayName(name: String) {
        if (name.isBlank()) {
            setEffect(ProfileEffect.ShowError("Display name cannot be empty."))
            return
        }
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            when (val result = userProfileRepository.updateDisplayName(name)) {
                is NetworkResult.Success -> {
                    setState { copy(profile = result.data, isSaving = false) }
                    setEffect(ProfileEffect.ShowSuccess("Profile updated successfully."))
                }
                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError("Failed to update profile."))
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            sessionRepository.clearSession()
            setEffect(ProfileEffect.NavigateToLogin)
        }
    }
}
