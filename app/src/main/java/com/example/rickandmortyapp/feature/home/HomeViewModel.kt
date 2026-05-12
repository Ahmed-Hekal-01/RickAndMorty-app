package com.example.rickandmortyapp.feature.home

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository
) : MviViewModel<HomeState, HomeEvent, HomeEffect>() {

    override fun createInitialState() = HomeState()

    init {
        onEvent(HomeEvent.LoadInitial)
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadInitial -> loadInitial()
            is HomeEvent.LoadNextPage -> loadNextPage()
            is HomeEvent.Retry -> loadInitial()
            is HomeEvent.CharacterClicked ->
                setEffect(HomeEffect.NavigateToDetail(event.characterId))
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, characters = emptyList()) }
            fetchPage(page = 1, isInitial = true)
        }
    }

    private fun loadNextPage() {
        val s = state.value
        if (s.isLoading || s.isLoadingMore || !s.hasMorePages) return

        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }
            fetchPage(page = s.currentPage + 1, isInitial = false)
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
                setState {
                    copy(isLoading = false, isLoadingMore = false, error = if (isInitial) message else error)
                }
                setEffect(HomeEffect.ShowError(message))
            }
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError -> "No internet connection."
    is NetworkResult.Error.BackendError.NotFound -> "Characters not found."
    is NetworkResult.Error.BackendError.TooManyRequests -> "Too many requests. Please slow down."
    is NetworkResult.Error.BackendError.Unavailable -> "Service unavailable."
    is NetworkResult.Error.BackendError.UnKnown -> "Something went wrong."
}
