package com.example.rickandmortyapp.feature.search

import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `QueryChanged triggers debounced search after 300ms`() = runTest {
        val repository = FakeCharacterRepository().apply {
            searchResponses[SearchKey("rick", null, 1)] =
                NetworkResult.Success(pageOf(character(1, "Rick"), next = null))
        }
        val viewModel = SearchViewModel(repository)

        viewModel.onEvent(SearchEvent.QueryChanged("rick"))

        advanceTimeBy(299)
        assertEquals(0, repository.searchRequests.size)

        advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(listOf(SearchKey("rick", null, 1)), repository.searchRequests)
        assertEquals(listOf("Rick"), viewModel.state.value.results.map { it.name })
        assertTrue(viewModel.state.value.hasSearched)
    }

    @Test
    fun `NotFound on initial search is treated as empty state and disables pagination`() = runTest {
        val repository = FakeCharacterRepository().apply {
            searchResponses[SearchKey("unknown", null, 1)] = NetworkResult.Error.BackendError.NotFound
        }
        val viewModel = SearchViewModel(repository)

        viewModel.onEvent(SearchEvent.QueryChanged("unknown"))
        viewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        assertEquals(emptyList<Character>(), viewModel.state.value.results)
        assertEquals(1, viewModel.state.value.currentPage)
        assertFalse(viewModel.state.value.hasMorePages)
        assertTrue(viewModel.state.value.hasSearched)
        assertEquals(1, repository.searchRequests.size)
    }

    @Test
    fun `LoadNextPage appends data for same query and filter`() = runTest {
        val repository = FakeCharacterRepository().apply {
            searchResponses[SearchKey("rick", CharacterStatus.ALIVE, 1)] =
                NetworkResult.Success(pageOf(character(1, "Rick"), next = "next"))
            searchResponses[SearchKey("rick", CharacterStatus.ALIVE, 2)] =
                NetworkResult.Success(pageOf(character(2, "Morty"), next = null))
        }
        val viewModel = SearchViewModel(repository)

        viewModel.onEvent(SearchEvent.QueryChanged("rick"))
        viewModel.onEvent(SearchEvent.StatusFilterChanged(CharacterStatus.ALIVE))
        viewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        viewModel.onEvent(SearchEvent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(
            listOf(
                SearchKey("rick", CharacterStatus.ALIVE, 1),
                SearchKey("rick", CharacterStatus.ALIVE, 2)
            ),
            repository.searchRequests
        )
        assertEquals(listOf("Rick", "Morty"), viewModel.state.value.results.map { it.name })
        assertEquals(2, viewModel.state.value.currentPage)
        assertFalse(viewModel.state.value.hasMorePages)
    }

    @Test
    fun `emits mapped error effect for non-NotFound failures`() = runTest {
        val repository = FakeCharacterRepository().apply {
            searchResponses[SearchKey("rick", null, 1)] = NetworkResult.Error.OfflineError
        }
        val viewModel = SearchViewModel(repository)

        viewModel.effect.test {
            viewModel.onEvent(SearchEvent.QueryChanged("rick"))
            viewModel.onEvent(SearchEvent.Search)
            advanceUntilIdle()

            assertEquals(SearchEffect.ShowError("No internet connection."), awaitItem())
        }

        assertEquals("No internet connection.", viewModel.state.value.error)
    }

    private data class SearchKey(val query: String, val status: CharacterStatus?, val page: Int)

    private class FakeCharacterRepository : ICharacterRepository {
        val searchResponses = mutableMapOf<SearchKey, NetworkResult<Page<Character>>>()
        val searchRequests = mutableListOf<SearchKey>()

        override suspend fun getAllCharacters(): NetworkResult<Page<Character>> = error("Not used")

        override suspend fun getCharacterByID(id: Int): NetworkResult<Character> = error("Not used")

        override suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>> = error("Not used")

        override suspend fun getListOfCharactersByIds(ids: List<Int>): NetworkResult<List<Character>> =
            error("Not used")

        override suspend fun searchCharacters(
            name: String,
            status: CharacterStatus?,
            page: Int
        ): NetworkResult<Page<Character>> {
            val key = SearchKey(name, status, page)
            searchRequests += key
            return searchResponses[key] ?: NetworkResult.Error.BackendError.UnKnown
        }
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

