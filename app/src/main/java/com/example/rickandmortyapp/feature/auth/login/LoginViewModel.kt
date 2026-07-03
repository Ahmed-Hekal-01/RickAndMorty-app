package com.example.rickandmortyapp.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
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
    private val sessionRepository: ISessionRepository,
    private val stringProvider: StringProvider
) : MviViewModel<LoginState, LoginEvent, LoginEffect>() {

    override fun createInitialState() = LoginState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                setState {
                    copy(
                        email = event.email,
                        emailError = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                setState {
                    copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }

            is LoginEvent.LoginClicked -> {
                attemptLogin()
            }

            is LoginEvent.ForgetPasswordClicked -> {
                setEffect(LoginEffect.NavigateToForgetPassword)
            }

            is LoginEvent.GoogleLoginClicked -> {
                setEffect(LoginEffect.LaunchGoogleSignIn)
            }

            is LoginEvent.GoogleTokenReceived -> {
                authenticateWithGoogle(event.idToken)
            }

            is LoginEvent.SignUpClicked -> {
                setEffect(LoginEffect.NavigateToSignUp)
            }

            is LoginEvent.NavigateToRegister -> {
                setEffect(LoginEffect.NavigateToSignUp)
            }
        }
    }

    private fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            setState { copy(isGoogleLoading = true, isEmailLoading = false) }

            when (val result = authRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    val firebaseToken = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()

                    sessionRepository.saveAuthToken(firebaseToken)
                    setState { copy(isGoogleLoading = false) }
                    setEffect(LoginEffect.NavigateToHome)
                }

                is NetworkResult.Error -> {
                    setState { copy(isGoogleLoading = false) }
                    setEffect(LoginEffect.ShowError(result.toErrorMessage(stringProvider)))
                }
            }
        }
    }
    private fun validateInputs(): Boolean {
        val email = state.value.email.trim()
        val password = state.value.password

        var isValid = true

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setState { copy(emailError = stringProvider.getString(R.string.error_invalid_email)) }
            isValid = false
        }

        if (password.length < 6) {
            setState { copy(passwordError = stringProvider.getString(R.string.error_password_length)) }
            isValid = false
        }

        return isValid
    }

    private fun attemptLogin() {
        if (!validateInputs()) return

        viewModelScope.launch {
            setState { copy(isEmailLoading = true, isGoogleLoading = false) }

            when (val result = authRepository.login(state.value.email.trim(), state.value.password)) {
                is NetworkResult.Success -> {
                    val token = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()
                    sessionRepository.saveAuthToken(token)
                    setState { copy(isEmailLoading = false) }
                    setEffect(LoginEffect.NavigateToHome)
                }

                is NetworkResult.Error -> {
                    setState { copy(isEmailLoading = false) }
                    setEffect(LoginEffect.ShowError(result.toErrorMessage(stringProvider)))
                }
            }
        }
    }

}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toErrorMessage(stringProvider: StringProvider): String = when (this) {
    is NetworkResult.Error.OfflineError -> stringProvider.getString(R.string.error_no_internet)
    is NetworkResult.Error.BackendError.NotFound -> stringProvider.getString(R.string.error_account_not_found)
    is NetworkResult.Error.BackendError.TooManyRequests -> stringProvider.getString(R.string.error_too_many_attempts)
    is NetworkResult.Error.BackendError.Unavailable -> stringProvider.getString(R.string.error_service_unavailable)
    is NetworkResult.Error.BackendError.UnKnown -> stringProvider.getString(R.string.error_invalid_credentials)
    is NetworkResult.Error.UserCancellation -> ""
}
