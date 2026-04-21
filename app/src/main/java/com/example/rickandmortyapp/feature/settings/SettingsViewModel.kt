package com.example.rickandmortyapp.feature.settings

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages settings screen state.
 *
 * Observes the [ISettingsRepository.settings] Flow so that any external
 * change (e.g. from another screen) is reflected automatically.
 * Toggle events write through to DataStore and the Flow emission updates state.
 *
 * [observeSettings] cancels any existing collector job before launching a new one,
 * so [SettingsEvent.LoadSettings] is safe to call multiple times without creating
 * duplicate emission pipelines.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository
) : MviViewModel<SettingsState, SettingsEvent, SettingsEffect>() {

    override fun createInitialState() = SettingsState()

    /** Tracks the active settings observer so we can cancel before re-subscribing. */
    private var settingsJob: Job? = null

    init {
        observeSettings()
    }

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            // Re-subscribe only if not already active (idempotent)
            is SettingsEvent.LoadSettings -> if (settingsJob?.isActive != true) observeSettings()
            is SettingsEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is SettingsEvent.ToggleNotifications -> toggleNotifications(event.enabled)
        }
    }

    private fun observeSettings() {
        settingsJob?.cancel()
        settingsJob = settingsRepository.settings
            .onEach { settings -> setState { fromSettings(settings) } }
            .launchIn(viewModelScope)
    }

    private fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setDarkMode(enabled)
            } catch (e: Exception) {
                setEffect(SettingsEffect.ShowError("Failed to save setting."))
            }
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setNotificationsEnabled(enabled)
            } catch (e: Exception) {
                setEffect(SettingsEffect.ShowError("Failed to save setting."))
            }
        }
    }
}
