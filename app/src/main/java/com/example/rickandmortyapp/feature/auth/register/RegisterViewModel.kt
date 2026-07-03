package com.example.rickandmortyapp.feature.auth.register

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.data.repository.ISessionRepository
import com.example.rickandmortyapp.data.repository.IUserProfileRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Manages registration screen state and business logic.
 *
 * Validates all three fields locally (email format, password length, match)
 * before making the Firebase create-account call.
 * On success the ID token is persisted and [RegisterEffect.NavigateToHome] is emitted.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val sessionRepository: ISessionRepository,
    private val userProfileRepository: IUserProfileRepository,
    private val stringProvider: StringProvider
) : MviViewModel<RegisterState, RegisterEvent, RegisterEffect>() {

    override fun createInitialState() = RegisterState()

    override fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged ->
                setState { copy(fullName = event.fullName, fullNameError = null) }

            is RegisterEvent.EmailChanged ->
                setState { copy(email = event.email, emailError = null) }

            is RegisterEvent.PasswordChanged ->
                setState { copy(password = event.password, passwordError = null) }

            is RegisterEvent.ConfirmPasswordChanged ->
                setState { copy(confirmPassword = event.confirmPassword, confirmPasswordError = null) }

            is RegisterEvent.RegisterClicked -> {
                attemptRegister()
            }

            is RegisterEvent.GoogleRegisterClicked -> {
                setEffect(RegisterEffect.LaunchGoogleSignIn)
            }

            is RegisterEvent.GoogleTokenReceived -> {
                authenticateWithGoogle(event.idToken)
            }

            is RegisterEvent.NavigateToLogin ->
                setEffect(RegisterEffect.NavigateToLogin)
        }
    }

    private fun attemptRegister() {
        if (!validateInputs()) return

        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    isGoogleLoading = false
                )
            }

            when (val result = authRepository.register(
                state.value.fullName.trim(),
                state.value.email.trim(),
                password = state.value.password
            )) {
                is NetworkResult.Success -> {
                    // Retrieve the ID token using a suspend-safe await() call.
                    // If token fetch fails we still navigate home — Firebase auth already succeeded.
                    val token = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()
                    sessionRepository.saveAuthToken(token)
                    sessionRepository.setOnboarded(true)
                    setState { copy(isLoading = false) }
                    setEffect(RegisterEffect.NavigateToHome)
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(RegisterEffect.ShowError(result.toErrorMessage(stringProvider)))
                }
            }
        }
    }

    private fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            setState {
                copy(
                    isGoogleLoading = true,
                    isLoading = false
                )
            }

            when (val result = authRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    val firebaseToken = runCatching {
                        result.data.getIdToken(false).await().token
                    }.getOrNull()

                    sessionRepository.saveAuthToken(firebaseToken)
                    sessionRepository.setOnboarded(true)

                    setState { copy(isGoogleLoading = false) }
                    setEffect(RegisterEffect.NavigateToHome)
                }

                is NetworkResult.Error -> {
                    setState { copy(isGoogleLoading = false) }
                    setEffect(RegisterEffect.ShowError(result.toErrorMessage(stringProvider)))
                }
            }
        }
    }
    /** Returns true only when all fields pass validation rules. */
    private fun validateInputs(): Boolean {
        val s = state.value
        var isValid = true

        if (s.fullName.trim().length < 3) {
            setState { copy(fullNameError = stringProvider.getString(R.string.error_name_length)) }
            isValid = false
        }

        if (s.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) {
            setState { copy(emailError = stringProvider.getString(R.string.error_invalid_email)) }
            isValid = false
        }

        if (s.password.length < 6) {
            setState { copy(passwordError = stringProvider.getString(R.string.error_password_length)) }
            isValid = false
        }

        if (s.password != s.confirmPassword) {
            setState { copy(confirmPasswordError = stringProvider.getString(R.string.error_passwords_mismatch)) }
            isValid = false
        }

        return isValid
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toErrorMessage(stringProvider: StringProvider): String = when (this) {
    is NetworkResult.Error.OfflineError -> stringProvider.getString(R.string.error_no_internet)
    is NetworkResult.Error.BackendError.NotFound -> stringProvider.getString(R.string.error_registration_failed)
    is NetworkResult.Error.BackendError.TooManyRequests -> stringProvider.getString(R.string.error_too_many_attempts)
    is NetworkResult.Error.BackendError.Unavailable -> stringProvider.getString(R.string.error_service_unavailable)
    is NetworkResult.Error.BackendError.UnKnown -> stringProvider.getString(R.string.error_email_in_use)
    else -> {""}
}
