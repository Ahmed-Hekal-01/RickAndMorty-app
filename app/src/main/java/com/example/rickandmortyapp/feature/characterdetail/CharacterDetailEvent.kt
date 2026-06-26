package com.example.rickandmortyapp.feature.characterdetail

import com.example.rickandmortyapp.feature.base.UiEvent

/** Events the character detail screen can send to [CharacterDetailViewModel]. */
sealed class CharacterDetailEvent : UiEvent {
    /** Load character with the given ID. Sent automatically on screen creation. */
    data class LoadCharacter(val characterId: Int) : CharacterDetailEvent()
    /** User tapped the retry button. */
    data object Retry : CharacterDetailEvent()
    /** User tapped the back button. */
    data object NavigateBack : CharacterDetailEvent()
    /** User tapped the heart icon — toggle this character's favourite status. */
    data object ToggleFavorite : CharacterDetailEvent()
}
