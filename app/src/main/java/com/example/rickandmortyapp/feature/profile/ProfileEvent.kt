package com.example.rickandmortyapp.feature.profile

import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the profile screen can send to [ProfileViewModel]. */
sealed class ProfileEvent : UiEvent {
    data object LoadProfile : ProfileEvent()
    data class UpdateDisplayName(val name: String) : ProfileEvent()
    data object Logout : ProfileEvent()
}
