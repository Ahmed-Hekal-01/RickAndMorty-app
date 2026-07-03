package com.example.rickandmortyapp.feature.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
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


/**
 * Entry-point for the Favorites screen — connects the [FavoriteViewModel]
 * to the stateless [FavoriteScreenContent] composable.
 *
 * One-shot effects are collected in a [LaunchedEffect] that lives as long as
 * the composable is in composition, following the same pattern used by
 * HomeScreen and CharacterDetailsScreen.
 */
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCharacterDetails: (Int) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteEffect.NavigateToHome -> onNavigateToHome()
                is FavoriteEffect.NavigateToDetail -> onNavigateToCharacterDetails(effect.characterId)
                is FavoriteEffect.ShowSnackbar -> onShowSnackbar(effect.message)
            }
        }
    }

    FavoriteScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun FavoriteScreenContent(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit
) {
    val listState = rememberLazyListState()

    val isHeaderVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTopBar(stringResource(R.string.home_screen_top_bar_name))

            val collectionText = stringResource(R.string.your_collection)
            val favoritesText = stringResource(R.string.favorite_characters_title)

            AnimatedVisibility(
                visible = isHeaderVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.size.medium),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = favoritesText,
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = collectionText,
                        color = AppTheme.colorScheme.primaryLight,
                        style = AppTheme.typography.labelSmall
                    )

                }
            }


            AnimatedVisibility(
                visible = state.favorites.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyFavoriteContent(
                    onExploreClick = { onEvent(FavoriteEvent.ExploreClicked) }
                )
            }

            AnimatedVisibility(
                visible = state.favorites.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FavoriteCharactersList(
                    characters = state.favorites,
                    listState = listState,
                    onRemove = { character -> onEvent(FavoriteEvent.RemoveFavorite(character)) },
                    onCharacterClick = { characterId -> onEvent(FavoriteEvent.CharacterClicked(characterId)) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteCharactersList(
    characters: List<Character>,
    listState: LazyListState,
    onRemove: (Character) -> Unit,
    onCharacterClick: (Int) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = AppTheme.size.large),
        verticalArrangement = Arrangement.spacedBy(AppTheme.size.large)
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            FavoriteCharacterCard(
                character = character,
                onRemove = { onRemove(character) },
                onClick = { onCharacterClick(character.id) }
            )
        }
    }
}

@Composable
private fun FavoriteCharacterCard(
    character: Character,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.favoriteCardHeight)
            .clip(AppTheme.shape.container)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .size(AppTheme.size.favoriteAvatarSize)
                    .clip(AppTheme.shape.container),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(AppTheme.size.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(AppTheme.size.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.size.small)
                            .background(
                                color = character.status.color,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(AppTheme.size.small))

                    Text(
                        text = "${character.status.displayName.uppercase()} – ${character.species.uppercase()}",
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.labelSmall
                    )
                }
            }

            // Heart button: tapping it un-favourites the character
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(R.string.remove_from_favorites),
                    tint = AppTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoriteContent(
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(AppTheme.size.emptyIconSize)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AppTheme.colorScheme.gradientStart,
                            AppTheme.colorScheme.gradientEnd
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                contentDescription = null,
                tint = AppTheme.colorScheme.onPrimary,
                modifier = Modifier.size(AppTheme.size.navIconSize)
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.size.large))

        Text(
            text = stringResource(R.string.no_favorite_characters_yet),
            color = AppTheme.colorScheme.primaryLight,
            style = AppTheme.typography.titleNormal
        )

        Spacer(modifier = Modifier.height(AppTheme.size.small))

        Text(
            text = stringResource(R.string.add_characters_to_favorites),
            color = AppTheme.colorScheme.textSecondary,
            style = AppTheme.typography.paragraph
        )

        Spacer(modifier = Modifier.height(AppTheme.size.large))

        Button(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.size.buttonHeight)
                .padding(horizontal = AppTheme.size.large)
                .shadow(
                    elevation = AppTheme.size.normal,
                    shape = AppTheme.shape.button
                ),
            shape = AppTheme.shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.explore_multiverse),
                color = AppTheme.colorScheme.onPrimary,
                style = AppTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showSystemUi = true)
@Composable
fun FavoriteScreenPreview() {
    AppTheme {
        FavoriteScreenContent(
            state = FavoriteState(favorites = emptyList()),
            onEvent = {}
        )
    }
}