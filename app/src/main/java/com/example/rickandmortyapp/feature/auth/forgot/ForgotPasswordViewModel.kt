package com.example.rickandmortyapp.feature.auth.forgot

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : MviViewModel<ForgotPasswordState, ForgotPasswordEvent, ForgotPasswordEffect>() {

    override fun createInitialState(): ForgotPasswordState {
        return ForgotPasswordState()
    }

    override fun handleEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                setState {
                    copy(
                        email = event.email,
                        emailError = null
                    )
                }
            }

            is ForgotPasswordEvent.SendResetLinkClicked -> {
                sendResetLink()
            }

            is ForgotPasswordEvent.BackToLoginClicked -> {
                setEffect(ForgotPasswordEffect.NavigateBackToLogin)
            }
        }
    }

    private fun sendResetLink() {
        if (!validateEmail()) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val email = state.value.email.trim()

            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false) }

                    setEffect(
                        ForgotPasswordEffect.ShowMessage(
                            "Password reset link has been sent to your email."
                        )
                    )

                    setEffect(ForgotPasswordEffect.NavigateBackToLogin)
                }

                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }

                    setEffect(
                        ForgotPasswordEffect.ShowMessage(
                            result.toErrorMessage()
                        )
                    )
                }
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = state.value.email.trim()

        return when {
            email.isBlank() -> {
                setState { copy(emailError = "Email is required") }
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                setState { copy(emailError = "Enter a valid email address") }
                false
            }

            else -> true
        }
    }
}

private fun NetworkResult.Error.toErrorMessage(): String {
    return when (this) {
        is NetworkResult.Error.OfflineError ->
            "No internet connection. Please try again."

        is NetworkResult.Error.BackendError.NotFound ->
            "No account found with this email."

        is NetworkResult.Error.BackendError.TooManyRequests ->
            "Too many attempts. Please wait and try again."

        is NetworkResult.Error.BackendError.Unavailable ->
            "Service unavailable. Please try again later."

        is NetworkResult.Error.BackendError.UnKnown ->
            "Failed to send reset link. Please check your email."

        is NetworkResult.Error.UserCancellation ->
            ""
    }
}