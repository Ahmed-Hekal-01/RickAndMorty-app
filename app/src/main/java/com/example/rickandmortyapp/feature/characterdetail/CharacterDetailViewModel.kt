package com.example.rickandmortyapp.feature.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import com.example.rickandmortyapp.R
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
    savedStateHandle: SavedStateHandle,
    private val stringProvider: StringProvider
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
                    val message = result.toUserMessage(stringProvider)
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
            val message = if (wasFavorite) stringProvider.getString(R.string.msg_removed_favorite, character.name)
                          else stringProvider.getString(R.string.msg_added_favorite, character.name)
            setEffect(CharacterDetailEffect.ShowSnackbar(message))
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(stringProvider: StringProvider): String = when (this) {
    is NetworkResult.Error.OfflineError                     -> stringProvider.getString(R.string.error_no_internet_short)
    is NetworkResult.Error.BackendError.NotFound            -> stringProvider.getString(R.string.error_character_not_found)
    is NetworkResult.Error.BackendError.TooManyRequests     -> stringProvider.getString(R.string.error_too_many_requests)
    is NetworkResult.Error.BackendError.Unavailable         -> stringProvider.getString(R.string.error_service_unavailable_short)
    is NetworkResult.Error.BackendError.UnKnown             -> stringProvider.getString(R.string.error_something_went_wrong)
    else                                                     -> ""
}
