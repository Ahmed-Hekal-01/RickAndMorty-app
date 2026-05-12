package com.example.rickandmortyapp.feature.characterdetail

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [CharacterDetailViewModel]. */
sealed class CharacterDetailEffect : UiEffect {
    data class ShowError(val message: String) : CharacterDetailEffect()
    data object NavigateBack : CharacterDetailEffect()
}
