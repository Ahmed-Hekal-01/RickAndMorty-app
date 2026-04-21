package com.example.rickandmortyapp.feature.auth.login

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
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows inline validation errors and does not call repository when inputs are invalid`() = runTest {
        val authRepository = FakeAuthRepository()
        val sessionRepository = FakeSessionRepository()
        val viewModel = LoginViewModel(authRepository, sessionRepository)

        viewModel.onEvent(LoginEvent.EmailChanged("invalid"))
        viewModel.onEvent(LoginEvent.PasswordChanged("123"))
        viewModel.onEvent(LoginEvent.LoginClicked)

        advanceUntilIdle()

        assertEquals(0, authRepository.loginCalls)
        assertEquals("Enter a valid email address", viewModel.state.value.emailError)
        assertEquals("Password must be at least 6 characters", viewModel.state.value.passwordError)
    }

    @Test
    fun `persists token and navigates home on successful login`() = runTest {
        val tokenResult = mockk<GetTokenResult>()
        every { tokenResult.token } returns "firebase-token"

        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseUser.getIdToken(false) } returns Tasks.forResult(tokenResult)

        val authRepository = FakeAuthRepository(
            loginResult = NetworkResult.Success(firebaseUser)
        )
        val sessionRepository = FakeSessionRepository()
        val viewModel = LoginViewModel(authRepository, sessionRepository)

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.EmailChanged("morty@example.com"))
            viewModel.onEvent(LoginEvent.PasswordChanged("123456"))
            viewModel.onEvent(LoginEvent.LoginClicked)

            advanceUntilIdle()

            assertEquals(LoginEffect.NavigateToHome, awaitItem())
        }

        assertEquals(1, authRepository.loginCalls)
        assertEquals("firebase-token", sessionRepository.savedToken)
        assertTrue(!viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.emailError)
        assertNull(viewModel.state.value.passwordError)
    }

    @Test
    fun `emits mapped error effect when login fails`() = runTest {
        val authRepository = FakeAuthRepository(
            loginResult = NetworkResult.Error.BackendError.NotFound
        )
        val sessionRepository = FakeSessionRepository()
        val viewModel = LoginViewModel(authRepository, sessionRepository)

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.EmailChanged("morty@example.com"))
            viewModel.onEvent(LoginEvent.PasswordChanged("123456"))
            viewModel.onEvent(LoginEvent.LoginClicked)

            advanceUntilIdle()

            assertEquals(LoginEffect.ShowError("Account not found."), awaitItem())
        }

        assertEquals(1, authRepository.loginCalls)
        assertNull(sessionRepository.savedToken)
        assertTrue(!viewModel.state.value.isLoading)
    }

    private class FakeAuthRepository(
        private val loginResult: NetworkResult<FirebaseUser> = NetworkResult.Error.OfflineError
    ) : IAuthRepository {
        var loginCalls = 0

        override val currentUser: Flow<FirebaseUser?> = emptyFlow()
        override val isLoggedIn: Boolean = false

        override suspend fun login(email: String, password: String): NetworkResult<FirebaseUser> {
            loginCalls += 1
            return loginResult
        }

        override suspend fun register(email: String, password: String): NetworkResult<FirebaseUser> {
            error("Not used")
        }

        override suspend fun logout() = Unit
    }

    private class FakeSessionRepository : ISessionRepository {
        var savedToken: String? = null

        override val isOnboarded: Flow<Boolean> = MutableStateFlow(false)
        override val authToken: Flow<String?> = MutableStateFlow(null)

        override suspend fun setOnboarded(value: Boolean) = Unit

        override suspend fun saveAuthToken(token: String?) {
            savedToken = token
        }

        override suspend fun clearSession() = Unit
    }
}


