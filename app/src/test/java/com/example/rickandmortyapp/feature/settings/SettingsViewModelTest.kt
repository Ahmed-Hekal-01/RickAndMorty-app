package com.example.rickandmortyapp.feature.settings

import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.AppSettings
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads settings from repository flow on init`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        assertEquals(false, viewModel.state.value.darkMode)
        assertEquals(true, viewModel.state.value.notificationsEnabled)
        assertFalse(viewModel.state.value.isLoading)

        repository.settingsFlow.value = AppSettings(darkMode = true, notificationsEnabled = false)
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.darkMode)
        assertEquals(false, viewModel.state.value.notificationsEnabled)
    }

    @Test
    fun `emits ShowError when toggle fails`() = runTest {
        val repository = FakeSettingsRepository().apply { failOnDarkMode = true }
        val viewModel = SettingsViewModel(repository)

        viewModel.effect.test {
            advanceUntilIdle()
            viewModel.onEvent(SettingsEvent.ToggleDarkMode(true))
            advanceUntilIdle()
            assertEquals(SettingsEffect.ShowError("Failed to save setting."), awaitItem())
        }
    }

    private class FakeSettingsRepository : ISettingsRepository {
        val settingsFlow = MutableStateFlow(AppSettings())
        var failOnDarkMode = false

        override val settings = settingsFlow

        override suspend fun setDarkMode(enabled: Boolean) {
            if (failOnDarkMode) throw IllegalStateException("boom")
            settingsFlow.value = settingsFlow.value.copy(darkMode = enabled)
        }

        override suspend fun setNotificationsEnabled(enabled: Boolean) {
            settingsFlow.value = settingsFlow.value.copy(notificationsEnabled = enabled)
        }
    }
}

