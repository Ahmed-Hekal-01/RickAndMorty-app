package com.example.rickandmortyapp.feature.home

import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the home screen can send to [HomeViewModel]. */
sealed class HomeEvent : UiEvent {
    /** Load the first page of characters. Sent automatically on screen creation. */
    data object LoadInitial : HomeEvent()
    /** User has scrolled close to the bottom — fetch the next page. */
    data object LoadNextPage : HomeEvent()
    /** User tapped the retry button after an error. */
    data object Retry : HomeEvent()
    /** User tapped a character card. */
    data class CharacterClicked(val characterId: Int) : HomeEvent()
}
