package com.example.rickandmortyapp.feature.auth.register

import app.cash.turbine.test
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows inline errors and blocks submit when passwords do not match`() = runTest {
        val authRepository = FakeAuthRepository()
        val sessionRepository = FakeSessionRepository()
        val viewModel = RegisterViewModel(authRepository, sessionRepository)

        viewModel.onEvent(RegisterEvent.EmailChanged("rick@example.com"))
        viewModel.onEvent(RegisterEvent.PasswordChanged("123456"))
        viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged("654321"))
        viewModel.onEvent(RegisterEvent.RegisterClicked)

        advanceUntilIdle()

        assertEquals(0, authRepository.registerCalls)
        assertEquals("Passwords do not match", viewModel.state.value.confirmPasswordError)
    }

    @Test
    fun `persists token and marks onboarding on successful registration`() = runTest {
        val tokenResult = mockk<GetTokenResult>()
        every { tokenResult.token } returns "reg-token"

        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.getIdToken(false) } returns Tasks.forResult(tokenResult)

        val authRepository = FakeAuthRepository(
            registerResult = NetworkResult.Success(firebaseUser)
        )
        val sessionRepository = FakeSessionRepository()
        val viewModel = RegisterViewModel(authRepository, sessionRepository)

        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.EmailChanged("rick@example.com"))
            viewModel.onEvent(RegisterEvent.PasswordChanged("123456"))
            viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged("123456"))
            viewModel.onEvent(RegisterEvent.RegisterClicked)

            advanceUntilIdle()

            assertEquals(RegisterEffect.NavigateToHome, awaitItem())
        }

        assertEquals(1, authRepository.registerCalls)
        assertEquals("reg-token", sessionRepository.savedToken)
        assertTrue(sessionRepository.isOnboardedValue)
        assertNull(viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
        assertNull(viewModel.state.value.confirmPasswordError)
    }

    @Test
    fun `emits mapped error effect when registration fails`() = runTest {
        val authRepository = FakeAuthRepository(
            registerResult = NetworkResult.Error.BackendError.UnKnown
        )
        val sessionRepository = FakeSessionRepository()
        val viewModel = RegisterViewModel(authRepository, sessionRepository)

        viewModel.effect.test {
            viewModel.onEvent(RegisterEvent.EmailChanged("rick@example.com"))
            viewModel.onEvent(RegisterEvent.PasswordChanged("123456"))
            viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged("123456"))
            viewModel.onEvent(RegisterEvent.RegisterClicked)

            advanceUntilIdle()

            assertEquals(RegisterEffect.ShowError("This email may already be in use."), awaitItem())
        }

        assertEquals(1, authRepository.registerCalls)
        assertNull(sessionRepository.savedToken)
    }

    private class FakeAuthRepository(
        private val registerResult: NetworkResult<FirebaseUser> = NetworkResult.Error.OfflineError
    ) : IAuthRepository {
        var registerCalls = 0

        override val currentUser: Flow<FirebaseUser?> = emptyFlow()
        override val isLoggedIn: Boolean = false

        override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun register(email: String, password: String): NetworkResult<FirebaseUser> {
            registerCalls += 1
            return registerResult
        }

        override suspend fun logout() = Unit
    }

    private class FakeSessionRepository : ISessionRepository {
        var savedToken: String? = null
        var isOnboardedValue: Boolean = false

        override val isOnboarded: Flow<Boolean> = MutableStateFlow(false)
        override val authToken: Flow<String?> = MutableStateFlow(null)

        override suspend fun setOnboarded(value: Boolean) {
            isOnboardedValue = value
        }

        override suspend fun saveAuthToken(token: String?) {
            savedToken = token
        }

        override suspend fun clearSession() = Unit
    }
}


