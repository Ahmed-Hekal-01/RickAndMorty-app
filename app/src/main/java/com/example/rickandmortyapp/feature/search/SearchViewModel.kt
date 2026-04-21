package com.example.rickandmortyapp.feature.search

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages character search screen state.
 *
 * Search strategy:
 * - [SearchEvent.QueryChanged] / [SearchEvent.StatusFilterChanged] update state immediately
 *   and schedule a debounced auto-search (300 ms) so the user gets live results
 *   while typing without hammering the API on every keystroke.
 * - [SearchEvent.Search] cancels any pending debounce and fires immediately.
 * - [SearchEvent.LoadNextPage] appends the next page to the existing results.
 * - [SearchEvent.ClearSearch] resets all state back to the initial blank screen.
 *
 * The API returns 404 when no characters match — this is treated as an empty
 * result set (not a hard error) so the UI can show "No results found."
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository
) : MviViewModel<SearchState, SearchEvent, SearchEffect>() {

    override fun createInitialState() = SearchState()

    private var debounceJob: Job? = null

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
            is SearchEvent.Search -> {
                debounceJob?.cancel()
                performSearch()
            }
            is SearchEvent.LoadNextPage -> loadNextPage()
            is SearchEvent.ClearSearch -> {
                debounceJob?.cancel()
                setState { SearchState() }
            }
        }
    }

    // ─── Search logic ─────────────────────────────────────────────────────────

    private fun scheduleDebounceSearch() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(300L)
            performSearch()
        }
    }

    private fun performSearch() {
        val query = state.value.query.trim()
        if (query.isBlank()) {
            setState { copy(results = emptyList(), hasSearched = false, error = null) }
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, results = emptyList(), hasSearched = true) }
            fetchPage(page = 1, query = query, status = state.value.statusFilter, isInitial = true)
        }
    }

    private fun loadNextPage() {
        val s = state.value
        if (s.isLoading || s.isLoadingMore || !s.hasMorePages || s.query.isBlank()) return

        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }
            fetchPage(
                page = s.currentPage + 1,
                query = s.query.trim(),
                status = s.statusFilter,
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
        when (val result = characterRepository.searchCharacters(query, status, page)) {
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
                // 404 from the Rick & Morty API means "no characters match" — show empty list.
                // Also reset pagination so LoadNextPage cannot run with stale metadata.
                if (isInitial && result is NetworkResult.Error.BackendError.NotFound) {
                    setState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false,
                            results = emptyList(),
                            currentPage = 1,
                            hasMorePages = false
                        )
                    }
                    return
                }
                val message = result.toUserMessage()
                setState { copy(isLoading = false, isLoadingMore = false, error = message) }
                setEffect(SearchEffect.ShowError(message))
            }
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError -> "No internet connection."
    is NetworkResult.Error.BackendError.NotFound -> "No characters found."
    is NetworkResult.Error.BackendError.TooManyRequests -> "Too many requests. Please slow down."
    is NetworkResult.Error.BackendError.Unavailable -> "Service unavailable."
    is NetworkResult.Error.BackendError.UnKnown -> "Something went wrong."
}
