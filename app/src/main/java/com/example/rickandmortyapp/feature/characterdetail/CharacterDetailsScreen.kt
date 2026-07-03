package com.example.rickandmortyapp.feature.characterdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.color
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun CharacterDetailsScreenPreview() {
    AppTheme {
        CharacterDetailsScreenContent(
            character = Character(
                id = 1,
                name = "Rick Sanchez",
                imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                status = com.example.rickandmortyapp.data.model.CharacterStatus.ALIVE,
                species = "Human",
                gender = "Male",
                origin = "Earth (C-137)",
                location = "Citadel of Ricks",
                episodeIds = listOf("1", "2")
            ),
            isFavorite = true,
            onFavoriteClick = {},
            onEpisodesClick = {}
        )
    }
}

@Composable
fun CharacterDetailsScreen(
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToEpisodes: (Int) -> Unit = {},
    onShowSnackbar: suspend (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterDetailEffect.NavigateBack -> onNavigateBack()
                is CharacterDetailEffect.ShowError -> onShowSnackbar(effect.message)
                is CharacterDetailEffect.ShowSnackbar -> onShowSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                text = stringResource(R.string.rick_and_morty_top_bar),
                showBackButton = true,
                onBackClick = { viewModel.onEvent(CharacterDetailEvent.NavigateBack) }
            )
        },
        containerColor = AppTheme.colorScheme.screenBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = AppTheme.colorScheme.primary)
                }
                state.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error!!,
                            color = AppTheme.colorScheme.textPrimary,
                            style = AppTheme.typography.paragraph
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onEvent(CharacterDetailEvent.Retry) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.retry), color = Color.White)
                        }
                    }
                }
                state.character != null -> {
                    CharacterDetailsScreenContent(
                        character = state.character!!,
                        isFavorite = state.isFavorite,
                        onFavoriteClick = {
                            viewModel.onEvent(CharacterDetailEvent.ToggleFavorite)
                        },
                        onEpisodesClick = {
                            state.character?.let { onNavigateToEpisodes(it.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterDetailsScreenContent(
    character: Character,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onEpisodesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .size(150.dp)
                    .border(
                        width = 4.dp,
                        color = character.status.color,
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_app_logo),
                error = painterResource(R.drawable.ic_app_logo)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = character.name,
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.textPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                    tint = if (isFavorite) AppTheme.colorScheme.primary else AppTheme.colorScheme.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = AppTheme.colorScheme.divider)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.information),
            style = AppTheme.typography.titleNormal,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colorScheme.textPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        AttributeRow(label = stringResource(R.string.label_status), value = character.status.displayName)
        AttributeRow(label = stringResource(R.string.label_episode_count), value = character.episodeIds.size.toString())
        AttributeRow(label = stringResource(R.string.label_gender), value = character.gender)
        AttributeRow(label = stringResource(R.string.label_origin), value = character.origin)
        AttributeRow(label = stringResource(R.string.label_location), value = character.location)
        AttributeRow(label = stringResource(R.string.label_species), value = character.species)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onEpisodesClick,
            modifier = Modifier.fillMaxWidth(),
            shape = AppTheme.shape.button,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.view_episodes),
                color = AppTheme.colorScheme.primary
            )
        }
        
        Spacer(
            modifier = Modifier
                .height(16.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
fun AttributeRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = AppTheme.typography.labelNormal,
            color = AppTheme.colorScheme.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTheme.typography.paragraph,
            color = AppTheme.colorScheme.textPrimary
        )
    }
}