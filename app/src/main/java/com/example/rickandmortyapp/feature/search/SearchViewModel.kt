package com.example.rickandmortyapp.feature.search

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the character search screen.
 *
 * Supported filters:
 * - Name only.
 * - Status only.
 * - Name + status together.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository,
    private val favoritesRepository: IFavoritesRepository
) : MviViewModel<SearchState, SearchEvent, SearchEffect>() {

    override fun createInitialState() = SearchState()

    private var debounceJob: Job? = null
    private var searchJob: Job? = null

    init {
        observeFavoriteIds()
    }

    override fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                setState { copy(query = event.query) }
                scheduleDebounceSearch()
            }

            is SearchEvent.StatusFilterChanged -> {
                setState { copy(statusFilter = event.status) }
                scheduleDebounceSearch()
            }

            SearchEvent.Search -> {
                debounceJob?.cancel()
                performSearch()
            }

            SearchEvent.LoadNextPage -> loadNextPage()

            SearchEvent.ClearSearch -> {
                debounceJob?.cancel()
                searchJob?.cancel()
                setState { SearchState(favoriteIds = favoriteIds) }
            }

            SearchEvent.Retry -> performSearch()

            is SearchEvent.FavoriteClicked -> toggleFavorite(event)
        }
    }

    private fun observeFavoriteIds() {
        viewModelScope.launch {
            favoritesRepository.observeFavoriteIds.collect { ids ->
                setState { copy(favoriteIds = ids) }
            }
        }
    }

    private fun toggleFavorite(event: SearchEvent.FavoriteClicked) {
        viewModelScope.launch {
            val character = state.value.results.find { it.id == event.characterId } ?: return@launch
            val wasFavorite = event.characterId in state.value.favoriteIds
            favoritesRepository.toggleFavorite(character)
            val verb = if (wasFavorite) "removed from" else "added to"
            setEffect(SearchEffect.ShowError("${event.characterName} $verb favorites"))
        }
    }

    private fun scheduleDebounceSearch() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(300L)
            performSearch()
        }
    }

    private fun performSearch() {
        val query = state.value.query.trim()
        val status = state.value.statusFilter

        if (query.isBlank() && status == null) {
            setState {
                copy(
                    results = emptyList(),
                    isLoading = false,
                    isLoadingMore = false,
                    currentPage = 1,
                    hasMorePages = true,
                    hasSearched = false,
                    error = null
                )
            }
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    isLoadingMore = false,
                    error = null,
                    results = emptyList(),
                    currentPage = 1,
                    hasMorePages = true,
                    hasSearched = true
                )
            }
            fetchPage(page = 1, query = query, status = status, isInitial = true)
        }
    }

    private fun loadNextPage() {
        val current = state.value
        val canSearch = current.query.isNotBlank() || current.statusFilter != null
        if (
            !canSearch ||
            current.isLoading ||
            current.isLoadingMore ||
            !current.hasMorePages
        ) return

        viewModelScope.launch {
            setState { copy(isLoadingMore = true, error = null) }
            fetchPage(
                page = current.currentPage + 1,
                query = current.query.trim(),
                status = current.statusFilter,
                isInitial = false
            )
        }
    }

    private suspend fun fetchPage(
        page: Int,
        query: String,
        status: CharacterStatus?,
        isInitial: Boolean
    ) {
        val result = characterRepository.searchCharacters(query, status, page)

        // Ignore old network responses if the user changed the input mid-request.
        if (state.value.query.trim() != query || state.value.statusFilter != status) return

        when (result) {
            is NetworkResult.Success -> {
                val newResults = result.data.results
                val hasMore = result.data.next != null
                setState {
                    copy(
                        results = if (isInitial) newResults else results + newResults,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = page,
                        hasMorePages = hasMore,
                        error = null
                    )
                }
            }

            is NetworkResult.Error -> {
                if (isInitial && result is NetworkResult.Error.BackendError.NotFound) {
                    setState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false,
                            results = emptyList(),
                            currentPage = 1,
                            hasMorePages = false,
                            error = null
                        )
                    }
                    return
                }

                val message = result.toUserMessage()
                setState {
                    copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = message
                    )
                }
                setEffect(SearchEffect.ShowError(message))
            }
        }
    }
}

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    NetworkResult.Error.OfflineError -> "No internet connection."
    NetworkResult.Error.BackendError.NotFound -> "No characters found."
    NetworkResult.Error.BackendError.TooManyRequests -> "Too many requests. Please slow down."
    NetworkResult.Error.BackendError.Unavailable -> "Service unavailable."
    NetworkResult.Error.BackendError.UnKnown -> "Something went wrong."
    NetworkResult.Error.UserCancellation -> "Request cancelled."
}
