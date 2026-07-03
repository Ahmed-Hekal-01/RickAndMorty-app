package com.example.rickandmortyapp.feature.auth.forgot

import com.example.rickandmortyapp.feature.base.UiEvent

sealed class ForgotPasswordEvent : UiEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    data object SendResetLinkClicked : ForgotPasswordEvent()
    data object BackToLoginClicked : ForgotPasswordEvent()
}