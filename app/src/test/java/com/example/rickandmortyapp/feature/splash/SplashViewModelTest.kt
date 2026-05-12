package com.example.rickandmortyapp.feature.splash

import app.cash.turbine.test
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
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
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `navigates to HOME when firebase auth and token are both present`() = runTest {
        val authRepository = FakeAuthRepository(isLoggedIn = true)
        val sessionRepository = FakeSessionRepository(token = "token-123")

        val viewModel = SplashViewModel(authRepository, sessionRepository)

        advanceUntilIdle()

        assertEquals(Destination.HOME, viewModel.state.value.destination)
        assertFalse(viewModel.state.value.isLoading)
        viewModel.effect.test {
            assertEquals(SplashEffect.NavigateTo(Destination.HOME), awaitItem())
        }
    }

    @Test
    fun `navigates to LOGIN when persisted token is missing`() = runTest {
        val authRepository = FakeAuthRepository(isLoggedIn = true)
        val sessionRepository = FakeSessionRepository(token = null)

        val viewModel = SplashViewModel(authRepository, sessionRepository)

        advanceUntilIdle()

        assertEquals(Destination.LOGIN, viewModel.state.value.destination)
        assertFalse(viewModel.state.value.isLoading)
        viewModel.effect.test {
            assertEquals(SplashEffect.NavigateTo(Destination.LOGIN), awaitItem())
        }
    }

    private class FakeAuthRepository(
        override val isLoggedIn: Boolean
    ) : IAuthRepository {
        override val currentUser: Flow<FirebaseUser?> = emptyFlow()

        override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun register(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun logout() = Unit
    }

    private class FakeSessionRepository(token: String?) : ISessionRepository {
        override val isOnboarded: Flow<Boolean> = MutableStateFlow(false)
        override val authToken: Flow<String?> = MutableStateFlow(token)

        override suspend fun setOnboarded(value: Boolean) = Unit
        override suspend fun saveAuthToken(token: String?) = Unit
        override suspend fun clearSession() = Unit
    }
}

