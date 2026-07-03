package com.example.rickandmortyapp.feature.episodes

import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.util.StringProvider
import com.example.rickandmortyapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages the paginated episodes list screen.
 * Pagination matches the same strategy used in [HomeViewModel].
 */
@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val episodeRepository: IEpisodeRepository,
    private val stringProvider: StringProvider
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
        Timber.d("Fetching episodes - page: $page, isInitial: $isInitial")
        when (val result = episodeRepository.getEpisodeByPage(page)) {
            is NetworkResult.Success -> {
                val newEpisodes = result.data.results
                val hasMore = result.data.next != null
                Timber.d("Fetched ${newEpisodes.size} episodes successfully. Has more: $hasMore")
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
                Timber.e("Failed to fetch episodes for page $page. Error: $result")
                val message = result.toUserMessage(stringProvider)
                setState {
                    copy(isLoading = false, isLoadingMore = false, error = if (isInitial) message else error)
                }
                setEffect(EpisodesEffect.ShowError(message))
            }
        }
    }
}

// ─── Extension ───────────────────────────────────────────────────────────────

private fun NetworkResult.Error.toUserMessage(stringProvider: StringProvider): String = when (this) {
    is NetworkResult.Error.OfflineError -> stringProvider.getString(R.string.error_no_internet_short)
    is NetworkResult.Error.BackendError.NotFound -> stringProvider.getString(R.string.error_episodes_not_found)
    is NetworkResult.Error.BackendError.TooManyRequests -> stringProvider.getString(R.string.error_too_many_requests)
    is NetworkResult.Error.BackendError.Unavailable -> stringProvider.getString(R.string.error_service_unavailable_short)
    is NetworkResult.Error.BackendError.UnKnown -> stringProvider.getString(R.string.error_something_went_wrong)
    else -> ""
} //todo
