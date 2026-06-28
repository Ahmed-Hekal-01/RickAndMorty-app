package com.example.rickandmortyapp.feature.home

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the paginated character list (home) screen.
 *
 * Pagination strategy:
 * - [HomeEvent.LoadInitial] always fetches page 1 and replaces the list.
 * - [HomeEvent.LoadNextPage] is a no-op when already loading or when
 *   [HomeState.hasMorePages] is false, otherwise fetches [currentPage + 1]
 *   and **appends** results to avoid duplicates.
 *
 * **Favorite integration (Single Source of Truth):**
 * [HomeState.favoriteIds] is a [Set] of character IDs that are in the
 * current user's favourites. It is backed by Room via [IFavoritesRepository.observeFavoriteIds]
 * and updates automatically whenever [toggleFavorite] is called from **any** screen.
 * The [CharacterCard] composable uses an O(1) `Set.contains` lookup to decide
 * whether to render a filled or outline heart icon.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository,
    private val favoritesRepository: IFavoritesRepository
) : MviViewModel<HomeState, HomeEvent, HomeEffect>() {

    override fun createInitialState() = HomeState()

    init {
        onEvent(HomeEvent.LoadInitial)
        observeFavoriteIds()
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadInitial     -> loadInitial()
            is HomeEvent.LoadNextPage    -> loadNextPage()
            is HomeEvent.Retry           -> loadInitial()
            is HomeEvent.CharacterClicked ->
                setEffect(HomeEffect.NavigateToDetail(event.characterId))
            is HomeEvent.FavoriteClicked -> toggleFavorite(event)
        }
    }

    // ─── Favorites ────────────────────────────────────────────────────────────

    /**
     * Collects the live set of favourite IDs from Room.
     * Runs for the ViewModel's entire lifetime so the heart icon state
     * on every card is always in sync — even if the user toggled on a
     * different screen.
     */
    private fun observeFavoriteIds() {
        viewModelScope.launch {
            favoritesRepository.observeFavoriteIds.collect { ids ->
                setState { copy(favoriteIds = ids) }
            }
        }
    }

    private fun toggleFavorite(event: HomeEvent.FavoriteClicked) {
        viewModelScope.launch {
            val character = state.value.characters.find { it.id == event.characterId } ?: return@launch
            // Snapshot BEFORE the toggle — state.value.favoriteIds will have already
            // been updated by Room's Flow emission by the time toggleFavorite() returns.
            val wasFavorite = event.characterId in state.value.favoriteIds
            favoritesRepository.toggleFavorite(character)
            val verb = if (wasFavorite) "removed from" else "added to"
            setEffect(HomeEffect.ShowError("${event.characterName} $verb favorites"))
        }
    }

    // ─── Pagination ───────────────────────────────────────────────────────────

    private fun loadInitial() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, characters = emptyList()) }
            fetchPage(page = 1, isInitial = true)
        }
    }

    private fun loadNextPage() {
        val current = state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMorePages) return
        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }
            fetchPage(page = current.currentPage + 1, isInitial = false)
        }
    }

    private suspend fun fetchPage(page: Int, isInitial: Boolean) {
        when (val result = characterRepository.getCharacterByPage(page)) {
            is NetworkResult.Success -> {
                val newCharacters = result.data.results
                val hasMore = result.data.next != null
                setState {
                    copy(
                        characters = if (isInitial) newCharacters else characters + newCharacters,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = page,
                        hasMorePages = hasMore,
                        error = null
                    )
                }
            }
            is NetworkResult.Error -> {
                val message = result.toUserMessage()
                if (isInitial) {
                    setState {
                        copy(isLoading = false, isLoadingMore = false, error = message)
                    }
                    setEffect(HomeEffect.ShowError(message))
                } else {
                    setEffect(HomeEffect.ShowError(message))
                    delay(10000)
                    fetchPage(page, isInitial = false)
                }
            }
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError                     -> "No internet connection."
    is NetworkResult.Error.BackendError.NotFound            -> "Characters not found."
    is NetworkResult.Error.BackendError.TooManyRequests     -> "Too many requests. Please slow down."
    is NetworkResult.Error.BackendError.Unavailable         -> "Service unavailable."
    is NetworkResult.Error.BackendError.UnKnown             -> "Something went wrong."
    else                                                     -> "todo"
}
