package com.example.rickandmortyapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCharacterDetails: (Int) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onNavigateToCharacterDetails(effect.characterId)
                is HomeEffect.ShowError -> onShowSnackbar(effect.message)
            }
        }
    }

    HomeScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTopBar(stringResource(R.string.home_screen_top_bar_name))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Explore The MetaVirus",
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Home",
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.titleLarge
                )
            }
            CharacterGrid(
                state = state,
                onCharacterClicked = { id ->
                    onEvent(HomeEvent.CharacterClicked(id))
                },
                onFavoriteClicked = { id, name ->
                    onEvent(HomeEvent.FavoriteClicked(id, name))
                }
            )
        }
    }
}

@Composable
fun CharacterGrid(
    state: HomeState,
    onCharacterClicked: (Int) -> Unit,
    onFavoriteClicked: (Int, String) -> Unit
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = state.characters,
            key = { it.id }
        ) { character ->
            CharacterCard(
                character = character,
                onClick = { onCharacterClicked(character.id) },
                onFavoriteClick = {
                    onFavoriteClicked(character.id, character.name)
                }
            )

        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.cardShape)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = AppTheme.colorScheme.onSurfaceVariant,
                shape = AppTheme.shape.cardShape
            ),
        shape = AppTheme.shape.cardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = "character photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_app_logo),
                    error = painterResource(R.drawable.ic_app_logo)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .clickable(onClick = onFavoriteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                         /* todo this need to be changed on the status of the char if is in the fav or not */
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Add to favorites",
                        tint = AppTheme.colorScheme.primary
                    )
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Box(
                    modifier = Modifier
                )
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomePreview() {
    AppTheme() {
        HomeScreenContent(
            state = HomeState(
                characters = listOf(
                    Character(
                        id = 2,
                        name = "Morty Smith",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
                        status = CharacterStatus.ALIVE,
                        species = "Human",
                        gender = "Male",
                        origin = "Earth",
                        location = "Earth",
                        episodeIds = listOf("https://rickandmortyapi.com/api/episode/1")
                    )
                )
            ),
            onEvent = {}
        )
    }
}
