package com.example.rickandmortyapp.feature.profile

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import com.example.rickandmortyapp.data.repository.IUserProfileRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val sessionRepository: ISessionRepository,
    private val settingsRepository: ISettingsRepository,
    private val stringProvider: StringProvider
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
            is ProfileEvent.ChangeLanguage -> changeLanguage(event.languageCode)
            is ProfileEvent.Logout -> logout()
        }
    }
    private fun observeSettings() {
        settingsJob?.cancel()

        settingsJob = settingsRepository.settings
            .onEach { settings ->
                setState {
                    copy(
                        isDarkMode = settings.darkMode,
                        language = settings.language
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            try {
                settingsRepository.setLanguage(languageCode)
            } catch (e: Exception) {
                setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_change_language)))
            }
        }
    }

    private fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setDarkMode(enabled)
            } catch (e: Exception) {
                setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_change_theme)))
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
                    setEffect(ProfileEffect.ShowSuccess(stringProvider.getString(R.string.msg_avatar_updated)))
                }

                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_update_avatar)))
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
                    val message = stringProvider.getString(R.string.error_no_account_email)
                    setState { copy(isLoading = false, error = message) }
                    setEffect(ProfileEffect.ShowError(message))
                }
            }
        }
    }

    private fun updateDisplayName(name: String) {
        if (name.isBlank()) {
            setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_empty_display_name)))
            return
        }
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            when (val result = userProfileRepository.updateDisplayName(name)) {
                is NetworkResult.Success -> {
                    setState { copy(profile = result.data, isSaving = false) }
                    setEffect(ProfileEffect.ShowSuccess(stringProvider.getString(R.string.msg_profile_updated)))
                }
                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_update_profile)))
                }
            }
        }
    }
    private fun updateBio(bio: String) {
        val cleanedBio = bio.trim()

        if (cleanedBio.length > 160) {
            setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_bio_length)))
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
                    setEffect(ProfileEffect.ShowSuccess(stringProvider.getString(R.string.msg_bio_updated)))
                }

                is NetworkResult.Error -> {
                    setState { copy(isSaving = false) }
                    setEffect(ProfileEffect.ShowError(stringProvider.getString(R.string.error_update_bio)))
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
