package com.example.rickandmortyapp.feature.episodes

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [EpisodesViewModel]. */
sealed class EpisodesEffect : UiEffect {
    data class ShowError(val message: String) : EpisodesEffect()
}
