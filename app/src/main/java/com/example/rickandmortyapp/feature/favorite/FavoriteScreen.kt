package com.example.rickandmortyapp.feature.favorite

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyapp.ui.components.BottomNavBar
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme

data class FavoriteCharacter(
    val name: String,
    val species: String,
    val isAlive: Boolean,
    val image: Int
)

@Preview(showSystemUi = true)
@Composable
fun FavoriteScreenPreview() {
    AppTheme {
        FavoriteScreen()
    }
}

@Composable
fun FavoriteScreen(
    favoriteCharacters: List<FavoriteCharacter> = emptyList(),
    onExploreClick: () -> Unit = {},
    onNavClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large)
                .padding(bottom = AppTheme.size.bottomBarHeight + AppTheme.size.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            CustomTopBar("RICK & MORTY")

            if (favoriteCharacters.isEmpty()) {
                EmptyFavoriteContent(
                    onExploreClick = onExploreClick
                )
            } else {
                FavoriteCharactersList(
                    characters = favoriteCharacters
                )
            }
        }

        BottomNavBar(
            selectedRoute = "favorites",
            onNavigate = onNavClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FavoriteCharactersList(
    characters: List<FavoriteCharacter>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.size.large)
    ) {
        items(characters) { character ->
            FavoriteCharacterCard(character = character)
        }
    }
}

@Composable
private fun FavoriteCharacterCard(
    character: FavoriteCharacter
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

            Image(
                painter = painterResource(id = character.image),
                contentDescription = character.name,
                modifier = Modifier.size(AppTheme.size.favoriteAvatarSize),
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
                                color = if (character.isAlive)
                                    AppTheme.colorScheme.success
                                else
                                    AppTheme.colorScheme.error,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(AppTheme.size.small))

                    Text(
                        text = if (character.isAlive)
                            "ALIVE – ${character.species.uppercase()}"
                        else
                            "DEAD – ${character.species.uppercase()}",
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.labelSmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = AppTheme.colorScheme.primary
            )
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