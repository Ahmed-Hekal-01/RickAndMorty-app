package com.example.rickandmortyapp.feature.characterdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Page
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads character on init using savedState characterId`() = runTest {
        val repository = FakeCharacterRepository().apply {
            characterById[7] = NetworkResult.Success(character(7, "Snowball"))
        }

        val viewModel = CharacterDetailViewModel(
            characterRepository = repository,
            savedStateHandle = SavedStateHandle(mapOf("characterId" to 7))
        )

        advanceUntilIdle()

        assertEquals(listOf(7), repository.requestedIds)
        assertEquals("Snowball", viewModel.state.value.character?.name)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `emits ShowError effect when load fails`() = runTest {
        val repository = FakeCharacterRepository().apply {
            characterById[7] = NetworkResult.Error.BackendError.NotFound
        }

        val viewModel = CharacterDetailViewModel(
            characterRepository = repository,
            savedStateHandle = SavedStateHandle(mapOf("characterId" to 7))
        )

        viewModel.effect.test {
            advanceUntilIdle()
            assertEquals(CharacterDetailEffect.ShowError("Character not found."), awaitItem())
        }

        assertEquals("Character not found.", viewModel.state.value.error)
    }

    @Test
    fun `NavigateBack emits one-shot navigation effect`() = runTest {
        val repository = FakeCharacterRepository().apply {
            characterById[7] = NetworkResult.Success(character(7, "Snowball"))
        }

        val viewModel = CharacterDetailViewModel(
            characterRepository = repository,
            savedStateHandle = SavedStateHandle(mapOf("characterId" to 7))
        )

        viewModel.effect.test {
            advanceUntilIdle()
            viewModel.onEvent(CharacterDetailEvent.NavigateBack)
            assertEquals(CharacterDetailEffect.NavigateBack, awaitItem())
        }
    }

    private class FakeCharacterRepository : ICharacterRepository {
        val characterById = mutableMapOf<Int, NetworkResult<Character>>()
        val requestedIds = mutableListOf<Int>()

        override suspend fun getAllCharacters(): NetworkResult<Page<Character>> = error("Not used")

        override suspend fun getCharacterByID(id: Int): NetworkResult<Character> {
            requestedIds += id
            return characterById[id] ?: NetworkResult.Error.BackendError.UnKnown
        }

        override suspend fun getCharacterByPage(page: Int): NetworkResult<Page<Character>> = error("Not used")

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
        species = "Dog",
        gender = "Male",
        origin = "Earth",
        location = "Earth",
        episodeIds = listOf("1")
    )
}

