package com.example.rickandmortyapp.feature.auth.login

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [LoginViewModel]. */
sealed class LoginEffect : UiEffect {
    data object NavigateToHome : LoginEffect()
    data object NavigateToRegister : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
    data object NavigateToForgetPassword : LoginEffect()
    data object LaunchGoogleSignIn : LoginEffect()
    data object NavigateToSignUp : LoginEffect()
}
