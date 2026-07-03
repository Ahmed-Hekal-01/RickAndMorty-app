package com.example.rickandmortyapp.feature.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.data.repository.ICharacterRepository
import com.example.rickandmortyapp.data.repository.IEpisodeRepository
import com.example.rickandmortyapp.feature.base.MviViewModel
import com.example.rickandmortyapp.feature.base.UiEffect
import com.example.rickandmortyapp.feature.base.UiEvent
import com.example.rickandmortyapp.feature.base.UiState
import com.example.rickandmortyapp.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterEpisodesState(
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val characterName: String = "",
    val character: com.example.rickandmortyapp.data.model.Character? = null
) : UiState

sealed class CharacterEpisodesEvent : UiEvent {
    data class LoadEpisodes(val characterId: Int) : CharacterEpisodesEvent()
    data object Retry : CharacterEpisodesEvent()
    data object NavigateBack : CharacterEpisodesEvent()
}

sealed class CharacterEpisodesEffect : UiEffect {
    data class ShowError(val message: String) : CharacterEpisodesEffect()
    data object NavigateBack : CharacterEpisodesEffect()
}

@HiltViewModel
class CharacterEpisodesViewModel @Inject constructor(
    private val characterRepository: ICharacterRepository,
    private val episodeRepository: IEpisodeRepository,
    private val stringProvider: StringProvider,
    savedStateHandle: SavedStateHandle
) : MviViewModel<CharacterEpisodesState, CharacterEpisodesEvent, CharacterEpisodesEffect>() {

    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    override fun createInitialState() = CharacterEpisodesState()

    init {
        onEvent(CharacterEpisodesEvent.LoadEpisodes(characterId))
    }

    override fun handleEvent(event: CharacterEpisodesEvent) {
        when (event) {
            is CharacterEpisodesEvent.LoadEpisodes -> loadEpisodes(event.characterId)
            is CharacterEpisodesEvent.Retry -> loadEpisodes(characterId)
            is CharacterEpisodesEvent.NavigateBack -> setEffect(CharacterEpisodesEffect.NavigateBack)
        }
    }

    private fun loadEpisodes(id: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            
            // 1. Fetch character to get episode URLs
            when (val charResult = characterRepository.getCharacterByID(id)) {
                is NetworkResult.Success -> {
                    val character = charResult.data
                    setState { copy(character = character, characterName = character.name) }
                    
                    val episodeIds = character.episodeIds.mapNotNull { url ->
                        url.substringAfterLast("/").toIntOrNull()
                    }
                    
                    if (episodeIds.isEmpty()) {
                        setState { copy(episodes = emptyList(), isLoading = false) }
                        return@launch
                    }
                    
                    // 2. Fetch episodes list by IDs
                    when (val epResult = episodeRepository.getListOfEpisodesByIds(episodeIds)) {
                        is NetworkResult.Success -> {
                            setState { copy(episodes = epResult.data, isLoading = false) }
                        }
                        is NetworkResult.Error -> {
                            val message = epResult.toUserMessage(stringProvider)
                            setState { copy(isLoading = false, error = message) }
                            setEffect(CharacterEpisodesEffect.ShowError(message))
                        }
                    }
                }
                is NetworkResult.Error -> {
                    val message = charResult.toUserMessage(stringProvider)
                    setState { copy(isLoading = false, error = message) }
                    setEffect(CharacterEpisodesEffect.ShowError(message))
                }
            }
        }
    }
    
    private fun NetworkResult.Error.toUserMessage(stringProvider: StringProvider): String {
        return when (this) {
            is NetworkResult.Error.OfflineError -> stringProvider.getString(R.string.error_no_internet_short)
            is NetworkResult.Error.BackendError.NotFound -> stringProvider.getString(R.string.error_data_not_found)
            is NetworkResult.Error.BackendError.TooManyRequests -> stringProvider.getString(R.string.error_too_many_requests)
            is NetworkResult.Error.BackendError.Unavailable -> stringProvider.getString(R.string.error_service_unavailable_short)
            is NetworkResult.Error.BackendError.UnKnown -> stringProvider.getString(R.string.error_something_went_wrong)
            else -> stringProvider.getString(R.string.error_loading_data)
        }
    }
}
