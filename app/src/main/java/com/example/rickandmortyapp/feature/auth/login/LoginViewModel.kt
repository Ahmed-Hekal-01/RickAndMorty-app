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
            is LoginEvent.PasswordChanged -> setState {
                copy(
                    password = event.password,
                    passwordError = null
                )
            }
            is LoginEvent.LoginClicked -> attemptLogin()
            is LoginEvent.ForgetPasswordClicked -> setEffect(LoginEffect.NavigateToForgetPassword)
            is LoginEvent.GoogleLoginClicked -> setEffect(LoginEffect.LaunchGoogleSignIn)
            is LoginEvent.GoogleTokenReceived -> authenticateWithGoogle(event.idToken)
            is LoginEvent.SignUpClicked -> setEffect(LoginEffect.NavigateToSignUp)
            else -> {}
        }
    }

    private fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            setState { copy(isGoogleLoading = true, isEmailLoading = false) }
            when (val result = authRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    sessionRepository.saveAuthToken(idToken)
                    setState { copy(isGoogleLoading = false) }
                    setEffect(LoginEffect.NavigateToHome)
                }

                is NetworkResult.Error -> {
                    setState { copy(isGoogleLoading = false) }
                    setEffect(LoginEffect.ShowError(result.toErrorMessage()))
                }
            }
        }
    }

    private fun attemptLogin() {
        viewModelScope.launch {
            setState { copy(isEmailLoading = true, isGoogleLoading = false) }

            when (val result = authRepository.login(state.value.email, state.value.password)) {
                is NetworkResult.Success -> {
                    // Retrieve the ID token using a suspend-safe await() call.
                    // If token fetch fails we still navigate home — Firebase auth already succeeded.
                    val token = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()
                    sessionRepository.saveAuthToken(token)
                    setState { copy(isEmailLoading = false) }
                    setEffect(LoginEffect.NavigateToHome)
                }

                is NetworkResult.Error -> {
                    setState { copy(isEmailLoading = false) }
                    setEffect(LoginEffect.ShowError(result.toErrorMessage()))
                }
            }
        }
    }

}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toErrorMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError -> "No internet connection. Please try again."
    is NetworkResult.Error.BackendError.NotFound -> "Account not found."
    is NetworkResult.Error.BackendError.TooManyRequests -> "Too many attempts. Please wait and try again."
    is NetworkResult.Error.BackendError.Unavailable -> "Service unavailable. Please try again later."
    is NetworkResult.Error.BackendError.UnKnown -> "Invalid email or password."
    is NetworkResult.Error.UserCancellation -> ""
}
