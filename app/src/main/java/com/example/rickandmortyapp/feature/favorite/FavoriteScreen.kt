package com.example.rickandmortyapp.feature.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
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
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteEffect.NavigateToHome  -> onNavigateToHome()
                is FavoriteEffect.ShowSnackbar    -> onShowSnackbar(effect.message)
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
            CustomTopBar("RICK & MORTY")

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
                    onRemove = { character -> onEvent(FavoriteEvent.RemoveFavorite(character)) }
                )
            }
        }
    }
}

@Composable
private fun FavoriteCharactersList(
    characters: List<Character>,
    onRemove: (Character) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = AppTheme.size.large),
        verticalArrangement = Arrangement.spacedBy(AppTheme.size.large)
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            FavoriteCharacterCard(
                character = character,
                onRemove = { onRemove(character) }
            )
        }
    }
}

@Composable
private fun FavoriteCharacterCard(
    character: Character,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.favoriteCardHeight),
        shape = AppTheme.shape.container,
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
                    contentDescription = "Remove from favorites",
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
            text = "No favorite characters yet",
            color = AppTheme.colorScheme.primaryLight,
            style = AppTheme.typography.titleNormal
        )

        Spacer(modifier = Modifier.height(AppTheme.size.small))

        Text(
            text = "Add characters to favorites to see\nthem here",
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
                text = "EXPLORE MULTIVERSE",
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
            state = FavoriteState(),
            onEvent = {}
        )
    }
}