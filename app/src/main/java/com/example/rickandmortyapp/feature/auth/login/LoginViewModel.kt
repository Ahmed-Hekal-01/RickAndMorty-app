package com.example.rickandmortyapp.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Manages login screen state and business logic.
 *
 * Validates email/password locally before making a network call so the
 * user gets immediate inline feedback without a round-trip.
 * On success, the Firebase ID token is persisted via [ISessionRepository]
 * and [LoginEffect.NavigateToHome] is emitted.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val sessionRepository: ISessionRepository
) : MviViewModel<LoginState, LoginEvent, LoginEffect>() {

    override fun createInitialState() = LoginState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> setState { copy(email = event.email, emailError = null) }
            is LoginEvent.PasswordChanged -> setState { copy(password = event.password, passwordError = null) }
            is LoginEvent.LoginClicked -> attemptLogin()
            is LoginEvent.NavigateToRegister -> setEffect(LoginEffect.NavigateToRegister)
        }
    }

    private fun attemptLogin() {
        if (!validateInputs()) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when (val result = authRepository.login(state.value.email, state.value.password)) {
                is NetworkResult.Success -> {
                    // Retrieve the ID token using a suspend-safe await() call.
                    // If token fetch fails we still navigate home — Firebase auth already succeeded.
                    val token = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()
                    sessionRepository.saveAuthToken(token)
                    setState { copy(isLoading = false) }
                    setEffect(LoginEffect.NavigateToHome)
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(LoginEffect.ShowError(result.toErrorMessage()))
                }
            }
        }
    }

    /** Returns true if both fields pass basic validation. */
    private fun validateInputs(): Boolean {
        val email = state.value.email.trim()
        val password = state.value.password

        var isValid = true

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setState { copy(emailError = "Enter a valid email address") }
            isValid = false
        }
        if (password.length < 6) {
            setState { copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }
        return isValid
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toErrorMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError -> "No internet connection. Please try again."
    is NetworkResult.Error.BackendError.NotFound -> "Account not found."
    is NetworkResult.Error.BackendError.TooManyRequests -> "Too many attempts. Please wait and try again."
    is NetworkResult.Error.BackendError.Unavailable -> "Service unavailable. Please try again later."
    is NetworkResult.Error.BackendError.UnKnown -> "Invalid email or password."
}
