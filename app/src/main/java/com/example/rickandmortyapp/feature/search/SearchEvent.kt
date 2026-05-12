package com.example.rickandmortyapp.feature.search

import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the search screen can send to [SearchViewModel]. */
sealed class SearchEvent : UiEvent {
    /** User typed in the search field. */
    data class QueryChanged(val query: String) : SearchEvent()
    /** User toggled a status filter chip (null = clear filter). */
    data class StatusFilterChanged(val status: CharacterStatus?) : SearchEvent()
    /** User pressed the search / keyboard action button. */
    data object Search : SearchEvent()
    /** User scrolled to the bottom of the current results. */
    data object LoadNextPage : SearchEvent()
    /** User pressed the clear button. */
    data object ClearSearch : SearchEvent()
}
