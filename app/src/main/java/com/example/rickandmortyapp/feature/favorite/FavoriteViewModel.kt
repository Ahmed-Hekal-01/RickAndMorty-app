package com.example.rickandmortyapp.feature.favorite

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Favorites screen.
 *
 * **Single Source of Truth in practice:**
 * This ViewModel collects [IFavoritesRepository.observeFavorites] — a Room-backed
 * [kotlinx.coroutines.flow.Flow] — and pipes each emission directly into
 * [setState]. Because Room emits on every DB change, **any** write from any
 * other ViewModel (e.g. HomeViewModel or CharacterDetailViewModel calling
 * [IFavoritesRepository.toggleFavorite]) will instantly update this screen's
 * state without any manual coordination.
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoritesRepository: IFavoritesRepository,
    private val stringProvider: StringProvider
) : MviViewModel<FavoriteState, FavoriteEvent, FavoriteEffect>() {

    override fun createInitialState() = FavoriteState()

    init {
        observeFavorites()
    }

    // ─── Observe ──────────────────────────────────────────────────────────────

    /**
     * Launches a coroutine that lives as long as the ViewModel.
     * Every new list emission from Room triggers a [setState] call,
     * which Compose picks up automatically via [state] StateFlow.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.observeFavorites.collect { favorites ->
                setState { copy(favorites = favorites) }
            }
        }
    }

    // ─── Event handling ───────────────────────────────────────────────────────

    override fun handleEvent(event: FavoriteEvent) {
        when (event) {
            is FavoriteEvent.RemoveFavorite -> removeFavorite(event)
            is FavoriteEvent.ExploreClicked -> setEffect(FavoriteEffect.NavigateToHome)
        }
    }

    private fun removeFavorite(event: FavoriteEvent.RemoveFavorite) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(event.character)
            val message = stringProvider.getString(R.string.msg_removed_favorite, event.character.name)
            setEffect(FavoriteEffect.ShowSnackbar(message))
        }
    }
}
