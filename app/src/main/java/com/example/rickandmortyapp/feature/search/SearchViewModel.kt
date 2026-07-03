package com.example.rickandmortyapp.feature.search

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val favoritesRepository: IFavoritesRepository,
    private val stringProvider: StringProvider
) : MviViewModel<SearchState, SearchEvent, SearchEffect>() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

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
            val message = if (wasFavorite) stringProvider.getString(R.string.msg_removed_favorite, character.name)
            else stringProvider.getString(R.string.msg_added_favorite, character.name)
            setEffect(SearchEffect.ShowError(message))
        }
    }

    private fun scheduleDebounceSearch() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500L)
            performSearch()
        }
    }

    private fun performSearch() {
        val query = state.value.query.trim()
        val status = state.value.statusFilter

        if (query.isBlank() && status == null) {
            Timber.tag(TAG).d("performSearch: skipped — query is blank and no status filter")
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

        Timber.tag(TAG).d("performSearch: query=\"$query\", status=$status")

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
        ) {
            Timber.tag(TAG).d(
                "loadNextPage: skipped — canSearch=$canSearch, isLoading=${current.isLoading}, " +
                        "isLoadingMore=${current.isLoadingMore}, hasMorePages=${current.hasMorePages}"
            )
            return
        }

        val nextPage = current.currentPage + 1
        Timber.tag(TAG).d("loadNextPage: loading page $nextPage")

        viewModelScope.launch {
            setState { copy(isLoadingMore = true, error = null) }
            fetchPage(
                page = nextPage,
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
        Timber.tag(TAG)
            .d("fetchPage: >>> API REQUEST — page=$page, query=\"$query\", status=$status, isInitial=$isInitial")

        val result = characterRepository.searchCharacters(query, status, page)

        // Ignore old network responses if the user changed the input mid-request.
        if (state.value.query.trim() != query || state.value.statusFilter != status) {
            Timber.tag(TAG)
                .d("fetchPage: stale response discarded — query or status changed mid-request")
            return
        }

        when (result) {
            is NetworkResult.Success -> {
                val newResults = result.data.results
                val hasMore = result.data.next != null
                Timber.tag(TAG)
                    .d("fetchPage: <<< SUCCESS — page=$page, resultsCount=${newResults.size}, hasMore=$hasMore")
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
                Timber.tag(TAG).e("fetchPage: <<< ERROR — page=$page, error=$result")

                if (isInitial && result is NetworkResult.Error.BackendError.NotFound) {
                    Timber.tag(TAG)
                        .d("fetchPage: 404 on initial search — treating as empty results")
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

                val message = result.toUserMessage(stringProvider)
                if (isInitial) {
                    setState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = message
                        )
                    }
                    setEffect(SearchEffect.ShowError(message))
                } else {
                    // Pagination error — show snackbar then auto-retry after delay
                    // (matches HomeViewModel behaviour).
                    setState { copy(isLoadingMore = false) }
                    setEffect(SearchEffect.ShowError(message))
                    delay(10_000)
                    fetchPage(page, query, status, isInitial = false)
                }
            }
        }
    }
}

private fun NetworkResult.Error.toUserMessage(stringProvider: StringProvider): String = when (this) {
    is NetworkResult.Error.OfflineError -> stringProvider.getString(R.string.error_no_internet_short)
    is NetworkResult.Error.BackendError.NotFound -> stringProvider.getString(R.string.error_no_characters_found)
    is NetworkResult.Error.BackendError.TooManyRequests -> stringProvider.getString(R.string.error_too_many_requests)
    is NetworkResult.Error.BackendError.Unavailable -> stringProvider.getString(R.string.error_service_unavailable_short)
    is NetworkResult.Error.BackendError.UnKnown -> stringProvider.getString(R.string.error_something_went_wrong)
    is NetworkResult.Error.UserCancellation -> stringProvider.getString(R.string.error_request_cancelled)
}
