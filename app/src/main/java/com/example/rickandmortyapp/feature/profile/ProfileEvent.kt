package com.example.rickandmortyapp.feature.profile

import com.example.rickandmortyapp.feature.base.UiEvent

/** All events the profile screen can send to [ProfileViewModel]. */

sealed class ProfileEvent : UiEvent {
    data object LoadProfile : ProfileEvent()
    data class UpdateDisplayName(val name: String) : ProfileEvent()
    data class UpdateAvatar(val avatarUri: String) : ProfileEvent()
    data class ToggleDarkMode(val enabled: Boolean) : ProfileEvent()
    data object Logout : ProfileEvent()
    data class UpdateBio(val bio: String) : ProfileEvent()
    data class ChangeLanguage(val languageCode: String) : ProfileEvent()
}