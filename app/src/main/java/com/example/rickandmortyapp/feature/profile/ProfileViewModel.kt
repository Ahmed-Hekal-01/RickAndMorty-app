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
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    private val sessionRepository: ISessionRepository,
    private val settingsRepository: ISettingsRepository
) : MviViewModel<ProfileState, ProfileEvent, ProfileEffect>() {

    override fun createInitialState() = ProfileState()

    init {
        observeSettings()
        onEvent(ProfileEvent.LoadProfile)
    }
    private var settingsJob: Job? = null

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.UpdateDisplayName -> updateDisplayName(event.name)
            is ProfileEvent.UpdateAvatar -> updateAvatar(event.avatarUri)
            is ProfileEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is ProfileEvent.UpdateBio -> updateBio(event.bio)
            is ProfileEvent.Logout -> logout()
        }
    }
    private fun observeSettings() {
        settingsJob?.cancel()

        settingsJob = settingsRepository.settings
            .onEach { settings ->
                setState {
                    copy(isDarkMode = settings.darkMode)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setDarkMode(enabled)
            } catch (e: Exception) {
                setEffect(ProfileEffect.ShowError("Failed to change theme."))
            }
        }
    }

    private fun updateAvatar(avatarUri: String) {
        viewModelScope.launch {
            setState { copy(isSaving = true) }

            when (val result = userProfileRepository.updateAvatar(avatarUri)) {
                is NetworkResult.Success -> {
                    setState {
                        copy(
                            profile = result.data,
                            isSaving = false
                        )
                    }
                    setEffect(ProfileEffect.ShowSuccess("Profile image updated."))
                }

                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError("Failed to update profile image."))
                }
            }
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
    private fun updateBio(bio: String) {
        val cleanedBio = bio.trim()

        if (cleanedBio.length > 160) {
            setEffect(ProfileEffect.ShowError("Bio must be 160 characters or less."))
            return
        }

        viewModelScope.launch {
            setState { copy(isSaving = true) }

            when (val result = userProfileRepository.updateBio(cleanedBio)) {
                is NetworkResult.Success -> {
                    setState {
                        copy(
                            profile = result.data,
                            isSaving = false
                        )
                    }
                    setEffect(ProfileEffect.ShowSuccess("Bio updated successfully."))
                }

                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError("Failed to update bio."))
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
