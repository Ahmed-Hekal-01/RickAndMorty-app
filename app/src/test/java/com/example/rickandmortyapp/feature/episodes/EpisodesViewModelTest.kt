package com.example.rickandmortyapp.feature.episodes

import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads first page on init`() = runTest {
        val repository = FakeEpisodeRepository().apply {
            pageResponses[1] = NetworkResult.Success(pageOf(episode(1, "Pilot"), next = "next"))
        }

        val viewModel = EpisodesViewModel(repository)

        advanceUntilIdle()

        assertEquals(listOf(1), repository.requestedPages)
        assertEquals(listOf("Pilot"), viewModel.state.value.episodes.map { it.name })
        assertEquals(1, viewModel.state.value.currentPage)
        assertTrue(viewModel.state.value.hasMorePages)
    }

    @Test
    fun `emits ShowError effect on initial error`() = runTest {
        val repository = FakeEpisodeRepository().apply {
            pageResponses[1] = NetworkResult.Error.BackendError.Unavailable
        }

        val viewModel = EpisodesViewModel(repository)

        viewModel.effect.test {
            advanceUntilIdle()
            assertEquals(EpisodesEffect.ShowError("Service unavailable."), awaitItem())
        }
        assertEquals("Service unavailable.", viewModel.state.value.error)
    }

    private class FakeEpisodeRepository : IEpisodeRepository {
        val pageResponses = mutableMapOf<Int, NetworkResult<Page<Episode>>>()
        val requestedPages = mutableListOf<Int>()

        override suspend fun getAllEpisodes(): NetworkResult<Page<Episode>> = error("Not used")

        override suspend fun getEpisodeByID(id: Int): NetworkResult<Episode> = error("Not used")

        override suspend fun getEpisodeByPage(page: Int): NetworkResult<Page<Episode>> {
            requestedPages += page
            return pageResponses[page] ?: NetworkResult.Error.BackendError.UnKnown
        }

        override suspend fun getListOfEpisodesByIds(ids: List<Int>): NetworkResult<List<Episode>> =
            error("Not used")
    }

    private fun episode(id: Int, name: String) = Episode(
        id = id,
        name = name,
        seasonNumber = 1,
        episodeNumber = id,
        airDate = "2013",
        characterIds = listOf("1")
    )

    private fun pageOf(vararg episodes: Episode, next: String?) = Page(
        count = episodes.size,
        pages = 2,
        next = next,
        prev = null,
        results = episodes.toList()
    )
}

