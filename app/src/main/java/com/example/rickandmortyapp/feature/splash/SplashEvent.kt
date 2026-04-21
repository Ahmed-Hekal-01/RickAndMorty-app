package com.example.rickandmortyapp.feature.splash

import com.example.rickandmortyapp.feature.base.UiEvent

/** Events the splash screen can send to [SplashViewModel]. */
sealed class SplashEvent : UiEvent {
    /** Triggered once when the splash screen first appears. */
    data object CheckAuthState : SplashEvent()
}
