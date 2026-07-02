package com.example.rickandmortyapp.feature.auth.register

import com.example.rickandmortyapp.feature.base.UiEvent

/** All actions the user can take on the registration screen. */
sealed class RegisterEvent : UiEvent {
    data class FullNameChanged(val fullName: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()

    data object RegisterClicked : RegisterEvent()

    data object GoogleRegisterClicked : RegisterEvent()
    data class GoogleTokenReceived(val idToken: String) : RegisterEvent()

    data object NavigateToLogin : RegisterEvent()
}