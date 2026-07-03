package com.example.rickandmortyapp.feature.auth.forgot

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IAuthRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val stringProvider: StringProvider
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
                            stringProvider.getString(R.string.msg_reset_link_sent)
                        )
                    )

                    setEffect(ForgotPasswordEffect.NavigateBackToLogin)
                }

                is NetworkResult.Error -> {
                    setState { copy(isLoading = false) }

                    setEffect(
                        ForgotPasswordEffect.ShowMessage(
                            result.toErrorMessage(stringProvider)
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
                setState { copy(emailError = stringProvider.getString(R.string.error_email_required)) }
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                setState { copy(emailError = stringProvider.getString(R.string.error_invalid_email)) }
                false
            }

            else -> true
        }
    }
}

private fun NetworkResult.Error.toErrorMessage(stringProvider: StringProvider): String {
    return when (this) {
        is NetworkResult.Error.OfflineError ->
            stringProvider.getString(R.string.error_no_internet)

        is NetworkResult.Error.BackendError.NotFound ->
            stringProvider.getString(R.string.error_no_account_email)

        is NetworkResult.Error.BackendError.TooManyRequests ->
            stringProvider.getString(R.string.error_too_many_attempts)

        is NetworkResult.Error.BackendError.Unavailable ->
            stringProvider.getString(R.string.error_service_unavailable)

        is NetworkResult.Error.BackendError.UnKnown ->
            stringProvider.getString(R.string.error_failed_reset_link)

        is NetworkResult.Error.UserCancellation ->
            ""
    }
}