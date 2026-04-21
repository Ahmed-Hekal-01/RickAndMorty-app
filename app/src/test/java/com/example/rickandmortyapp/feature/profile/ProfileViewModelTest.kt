package com.example.rickandmortyapp.feature.profile

import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.UserProfile
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.data.repository.IUserProfileRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads profile on init`() = runTest {
        val repo = FakeUserProfileRepository().apply {
            getProfileResult = NetworkResult.Success(sampleProfile("Summer"))
        }
        val authRepo = FakeAuthRepository()
        val sessionRepo = FakeSessionRepository()

        val viewModel = ProfileViewModel(repo, authRepo, sessionRepo)

        advanceUntilIdle()

        assertEquals(1, repo.getCalls)
        assertEquals("Summer", viewModel.state.value.profile?.displayName)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `shows error effect for blank display name`() = runTest {
        val viewModel = ProfileViewModel(
            FakeUserProfileRepository(),
            FakeAuthRepository(),
            FakeSessionRepository()
        )
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ProfileEvent.UpdateDisplayName("   "))
            assertEquals(ProfileEffect.ShowError("Display name cannot be empty."), awaitItem())
        }
    }

    @Test
    fun `logout clears auth and session then navigates to login`() = runTest {
        val authRepo = FakeAuthRepository()
        val sessionRepo = FakeSessionRepository()

        val viewModel = ProfileViewModel(FakeUserProfileRepository(), authRepo, sessionRepo)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(ProfileEvent.Logout)
            advanceUntilIdle()
            assertEquals(ProfileEffect.NavigateToLogin, awaitItem())
        }

        assertEquals(1, authRepo.logoutCalls)
        assertEquals(1, sessionRepo.clearCalls)
    }

    private class FakeUserProfileRepository : IUserProfileRepository {
        var getCalls = 0
        var getProfileResult: NetworkResult<UserProfile> =
            NetworkResult.Success(sampleProfile("Rick"))

        override suspend fun getCurrentProfile(): NetworkResult<UserProfile> {
            getCalls += 1
            return getProfileResult
        }

        override suspend fun updateDisplayName(name: String): NetworkResult<UserProfile> {
            return NetworkResult.Success(sampleProfile(name))
        }
    }

    private class FakeAuthRepository : IAuthRepository {
        var logoutCalls = 0

        override val currentUser: Flow<FirebaseUser?> = emptyFlow()
        override val isLoggedIn: Boolean = false

        override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun register(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun logout() {
            logoutCalls += 1
        }
    }

    private class FakeSessionRepository : ISessionRepository {
        var clearCalls = 0

        override val isOnboarded = MutableStateFlow(false)
        override val authToken = MutableStateFlow<String?>(null)

        override suspend fun setOnboarded(value: Boolean) = Unit
        override suspend fun saveAuthToken(token: String?) = Unit

        override suspend fun clearSession() {
            clearCalls += 1
        }
    }

    private companion object {
        fun sampleProfile(name: String) = UserProfile(
            uid = "uid-1",
            email = "user@example.com",
            displayName = name,
            photoUrl = null
        )
    }
}

