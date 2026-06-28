package com.example.rickandmortyapp.feature.favorite

import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the Favorites screen can dispatch to [FavoriteViewModel]. */
sealed class FavoriteEvent : UiEvent {
    /** User tapped the heart icon to un-favourite a character. */
    data class RemoveFavorite(val character: Character) : FavoriteEvent()
    /** User tapped "Explore Multiverse" on the empty state. */
    data object ExploreClicked : FavoriteEvent()
}
