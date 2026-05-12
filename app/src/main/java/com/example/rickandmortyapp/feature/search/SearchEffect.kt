package com.example.rickandmortyapp.feature.search

import com.example.rickandmortyapp.feature.base.UiEffect

/** One-shot side-effects emitted by [SearchViewModel]. */
sealed class SearchEffect : UiEffect {
    data class ShowError(val message: String) : SearchEffect()
}
