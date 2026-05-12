package com.example.rickandmortyapp.feature.episodes

import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the episodes screen can send to [EpisodesViewModel]. */
sealed class EpisodesEvent : UiEvent {
    data object LoadInitial : EpisodesEvent()
    data object LoadNextPage : EpisodesEvent()
    data object Retry : EpisodesEvent()
}
