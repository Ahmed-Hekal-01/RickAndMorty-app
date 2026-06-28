package com.example.rickandmortyapp.feature.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages character detail screen state.
 *
 * The character ID is retrieved from [SavedStateHandle] using the key
 * `"characterId"` — this matches the Navigation Compose argument name
 * so the value is automatically injected by Hilt.
 *
 * Loading is triggered automatically in [init]; [CharacterDetailEvent.Retry]
 * re-triggers it using the same stored ID.
 *
 * **Favorite integration:**
 * [observeIsFavorite] collects a Room-backed [kotlinx.coroutines.flow.Flow]
 * so [CharacterDetailState.isFavorite] stays in sync with the Single Source
 * of Truth automatically — no manual refresh required after toggling on the
 * Home screen.
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository,
    private val favoritesRepository: IFavoritesRepository,
    savedStateHandle: SavedStateHandle
) : MviViewModel<CharacterDetailState, CharacterDetailEvent, CharacterDetailEffect>() {

    /** The ID passed via Navigation Compose argument. */
    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    override fun createInitialState() = CharacterDetailState()

    init {
        onEvent(CharacterDetailEvent.LoadCharacter(characterId))
        observeIsFavorite()
    }

    override fun handleEvent(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.LoadCharacter -> loadCharacter(event.characterId)
            is CharacterDetailEvent.Retry         -> loadCharacter(characterId)
            is CharacterDetailEvent.NavigateBack  -> setEffect(CharacterDetailEffect.NavigateBack)
            is CharacterDetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = characterRepository.getCharacterByID(id)) {
                is NetworkResult.Success -> {
                    setState { copy(character = result.data, isLoading = false) }
                }
                is NetworkResult.Error -> {
                    val message = result.toUserMessage()
                    setState { copy(isLoading = false, error = message) }
                    setEffect(CharacterDetailEffect.ShowError(message))
                }
            }
        }
    }

    /**
     * Collects the Room-backed favourite status for this character.
     * Lives for the ViewModel's lifetime and auto-updates [CharacterDetailState.isFavorite]
     * whenever any screen toggles the favourite — no explicit refresh needed.
     */
    private fun observeIsFavorite() {
        viewModelScope.launch {
            favoritesRepository.observeIsFavorite(characterId).collect { isFav ->
                setState { copy(isFavorite = isFav) }
            }
        }
    }

    private fun toggleFavorite() {
        val character = state.value.character ?: return
        viewModelScope.launch {
            // Snapshot BEFORE the toggle — state.value.isFavorite will have already
            // flipped by the time toggleFavorite() returns (Room Flow emission).
            val wasFavorite = state.value.isFavorite
            favoritesRepository.toggleFavorite(character)
            val message = if (wasFavorite) "${character.name} removed from favorites"
                          else "${character.name} added to favorites"
            setEffect(CharacterDetailEffect.ShowSnackbar(message))
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError                     -> "No internet connection."
    is NetworkResult.Error.BackendError.NotFound            -> "Character not found."
    is NetworkResult.Error.BackendError.TooManyRequests     -> "Too many requests. Please slow down."
    is NetworkResult.Error.BackendError.Unavailable         -> "Service unavailable."
    is NetworkResult.Error.BackendError.UnKnown             -> "Something went wrong."
    else                                                     -> ""
}
