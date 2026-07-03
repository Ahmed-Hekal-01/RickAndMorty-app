package com.example.rickandmortyapp.feature.favorite

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [FavoriteViewModel]. */
sealed class FavoriteEffect : UiEffect {
    /** Navigate the user away from the Favorites screen to the Home/Explore screen. */
    data object NavigateToHome : FavoriteEffect()
    /** Show a short snackbar with [message]. */
    data class ShowSnackbar(val message: String) : FavoriteEffect()
    /** Navigate the user to the character details screen. */
    data class NavigateToDetail(val characterId: Int) : FavoriteEffect()
}
