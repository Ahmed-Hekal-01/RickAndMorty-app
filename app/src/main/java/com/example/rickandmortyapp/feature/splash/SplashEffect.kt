package com.example.rickandmortyapp.feature.splash

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot effects emitted by [SplashViewModel]. */
sealed class SplashEffect : UiEffect {
    /** Navigate away from the splash screen to the resolved [Destination]. */
    data class NavigateTo(val destination: Destination) : SplashEffect()
}
