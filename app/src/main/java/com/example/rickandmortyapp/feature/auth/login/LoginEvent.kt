package com.example.rickandmortyapp.feature.auth.login

import com.example.rickandmortyapp.feature.base.UiEvent

/** All actions the user can take on the login screen. */
sealed class LoginEvent : UiEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object LoginClicked : LoginEvent()
    data object NavigateToRegister : LoginEvent()
}
