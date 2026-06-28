package com.example.rickandmortyapp.feature.home

import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IFavoritesRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads first page on init`() = runTest {
        val repository = FakeCharacterRepository().apply {
            pageResponses[1] = NetworkResult.Success(pageOf(character(1, "Rick"), next = "next"))
        }

        val viewModel = HomeViewModel(repository, FakeFavoritesRepository())

        advanceUntilIdle()

        assertEquals(1, repository.pageRequests)
        assertEquals(listOf("Rick"), viewModel.state.value.characters.map { it.name })
        assertEquals(1, viewModel.state.value.currentPage)
        assertTrue(viewModel.state.value.hasMorePages)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `appends next page when LoadNextPage succeeds`() = runTest {
        val repository = FakeCharacterRepository().apply {
            pageResponses[1] = NetworkResult.Success(pageOf(character(1, "Rick"), next = "next"))
            pageResponses[2] = NetworkResult.Success(pageOf(character(2, "Morty"), next = null))
        }

        val viewModel = HomeViewModel(repository, FakeFavoritesRepository())
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(listOf(1, 2), repository.requestedPages)
        assertEquals(listOf("Rick", "Morty"), viewModel.state.value.characters.map { it.name })
        assertEquals(2, viewModel.state.value.currentPage)
        assertTrue(!viewModel.state.value.hasMorePages)
    }

    @Test
    fun `emits ShowError effect on initial load failure`() = runTest {
        val repository = FakeCharacterRepository().apply {
            pageResponses[1] = NetworkResult.Error.OfflineError
        }

        val viewModel = HomeViewModel(repository, FakeFavoritesRepository())

        viewModel.effect.test {
            advanceUntilIdle()
            assertEquals(HomeEffect.ShowError("No internet connection."), awaitItem())
        }

        assertEquals("No internet connection.", viewModel.state.value.error)
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `emits navigation effect when character is clicked`() = runTest {
        val repository = FakeCharacterRepository().apply {
            pageResponses[1] = NetworkResult.Success(pageOf(character(1, "Rick"), next = null))
        }
        val viewModel = HomeViewModel(repository, FakeFavoritesRepository())

        viewModel.effect.test {
            advanceUntilIdle()
            viewModel.onEvent(HomeEvent.CharacterClicked(42))
            assertEquals(HomeEffect.NavigateToDetail(42), awaitItem())
        }
    }

    @Test
    fun `retries pagination after 7 seconds delay on error`() = runTest {
        var calls = 0
        val repository = FakeCharacterRepository().apply {
            pageResponses[1] = NetworkResult.Success(pageOf(character(1, "Rick"), next = "next"))
            overrideGetCharacterByPage = { page ->
                if (page == 2) {
                    calls++
                    if (calls == 1) {
                        NetworkResult.Error.OfflineError
                    } else {
                        NetworkResult.Success(pageOf(character(2, "Morty"), next = null))
                    }
                } else {
                    pageResponses[page] ?: NetworkResult.Error.BackendError.UnKnown
                }
            }
        }

        val viewModel = HomeViewModel(repository, FakeFavoritesRepository())
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.LoadNextPage)
        
        // Wait, is it loading more?
        assertTrue(viewModel.state.value.isLoadingMore)
        assertEquals(1, calls)
        
        // Advance time by 6.9 seconds - should still be loading more and not retried yet
        testScheduler.advanceTimeBy(6900)
        assertTrue(viewModel.state.value.isLoadingMore)
        assertEquals(listOf("Rick"), viewModel.state.value.characters.map { it.name })
        assertEquals(1, calls)
        
        // Advance remaining time - should retry and succeed
        testScheduler.advanceTimeBy(100)
        advanceUntilIdle()
        
        assertTrue(!viewModel.state.value.isLoadingMore)
        assertEquals(listOf("Rick", "Morty"), viewModel.state.value.characters.map { it.name })
        assertEquals(2, calls)
    }

    private class FakeFavoritesRepository : IFavoritesRepository {
        override val observeFavorites: kotlinx.coroutines.flow.Flow<List<Character>> = kotlinx.coroutines.flow.flowOf(emptyList())
        override val observeFavoriteIds: kotlinx.coroutines.flow.Flow<Set<Int>> = kotlinx.coroutines.flow.flowOf(emptySet())
        override fun observeIsFavorite(characterId: Int): kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
        override suspend fun toggleFavorite(character: Character) {}
        override suspend fun syncPendingToCloud() {}
    }

    private class FakeCharacterRepository : ICharacterRepository {
        val pageResponses = mutableMapOf<Int, NetworkResult<Page<Character>>>()
        val requestedPages = mutableListOf<Int>()
        val pageRequests: Int get() = requestedPages.size

        override suspend fun getAllCharacters(): NetworkResult<Page<Character>> =
            error("Not used")

        override suspend fun getCharacterByID(id: Int): NetworkResult<Character> =
            error("Not used")

        var overrideGetCharacterByPage: (suspend (Int) -> NetworkResult<Page<Character>>)? = null

        override suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>> {
            requestedPages += page
            overrideGetCharacterByPage?.let { return it(page) }
            return pageResponses[page] ?: NetworkResult.Error.BackendError.UnKnown
        }

        override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<Character>> =
            error("Not used")

        override suspend fun searchCharacters(
            name: String,
            status: CharacterStatus?,
            page: Int
        ): NetworkResult<Page<Character>> = error("Not used")
    }

    private fun character(id: Int, name: String) = Character(
        id = id,
        name = name,
        imageUrl = "https://example.com/$id.png",
        status = CharacterStatus.ALIVE,
        species = "Human",
        gender = "Male",
        origin = "Earth",
        location = "Earth",
        episodeIds = listOf("1")
    )

    private fun pageOf(vararg characters: Character, next: String?) = Page(
        count = characters.size,
        pages = 2,
        next = next,
        prev = null,
        results = characters.toList()
    )
}

