package com.example.rickandmortyapp.feature.home

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [HomeViewModel]. */
sealed class HomeEffect : UiEffect {
    /** Navigate to the character detail screen with the given ID. */
    data class NavigateToDetail(val characterId: Int) : HomeEffect()
    /** Show a snackbar with the error message. */
    data class ShowError(val message: String) : HomeEffect()
}
