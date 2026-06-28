package com.example.rickandmortyapp.feature.episodes

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the paginated episodes list screen.
 * Pagination matches the same strategy used in [HomeViewModel].
 */
@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val episodeRepository: IEpisodeRepository
) : MviViewModel<EpisodesState, EpisodesEvent, EpisodesEffect>() {

    override fun createInitialState() = EpisodesState()

    init {
        onEvent(EpisodesEvent.LoadInitial)
    }

    override fun handleEvent(event: EpisodesEvent) {
        when (event) {
            is EpisodesEvent.LoadInitial -> loadInitial()
            is EpisodesEvent.LoadNextPage -> loadNextPage()
            is EpisodesEvent.Retry -> loadInitial()
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null, episodes = emptyList()) }
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
        when (val result = episodeRepository.getEpisodeByPage(page)) {
            is NetworkResult.Success -> {
                val newEpisodes = result.data.results
                val hasMore = result.data.next != null
                setState {
                    copy(
                        episodes = if (isInitial) newEpisodes else episodes + newEpisodes,
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
                setEffect(EpisodesEffect.ShowError(message))
            }
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(): String = when (this) {
    is NetworkResult.Error.OfflineError -> "No internet connection."
    is NetworkResult.Error.BackendError.NotFound -> "Episodes not found."
    is NetworkResult.Error.BackendError.TooManyRequests -> "Too many requests. Please slow down."
    is NetworkResult.Error.BackendError.Unavailable -> "Service unavailable."
    is NetworkResult.Error.BackendError.UnKnown -> "Something went wrong."
    else -> {""} //todo
}
