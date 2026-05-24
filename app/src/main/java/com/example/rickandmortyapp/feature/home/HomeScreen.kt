package com.example.rickandmortyapp.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme

data class RosterCharacter(
    val name: String,
    val role: String,
    val status: String,
    val image: Int
)

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}

@Composable
fun HomeScreen(
    onNavClick: (String) -> Unit = {}
) {
    val characters = listOf(
        RosterCharacter("Elara Nox", "Netrunner", "SXANDBH", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo),
        RosterCharacter("Jax-99", "Heavy Ordinance", "MIA", R.drawable.app_logo)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTopBar("RICK & MORTY")

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = AppTheme.size.large,
                    end = AppTheme.size.large,
                    top = AppTheme.size.large,
                    bottom = AppTheme.size.bottomBarHeight + AppTheme.size.large
                ),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.size.medium),
                verticalArrangement = Arrangement.spacedBy(AppTheme.size.medium)
            ) {
                item {
                    Column {
                        Text(
                            text = "DATABASE ACCESS",
                            color = AppTheme.colorScheme.primaryLight,
                            style = AppTheme.typography.labelSmall
                        )

                        Text(
                            text = "Roster",
                            color = AppTheme.colorScheme.textSecondary,
                            style = AppTheme.typography.titleLarge
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(AppTheme.size.small))
                }

                items(characters) { character ->
                    RosterCharacterCard(character = character)
                }
            }
        }

    }
}

@Composable
private fun RosterCharacterCard(
    character: RosterCharacter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.rosterCardHeight),
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.cardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.small)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.size.rosterImageHeight)
            ) {
                Image(
                    painter = painterResource(id = character.image),
                    contentDescription = character.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AppTheme.shape.button),
                    contentScale = ContentScale.Crop
                )

                StatusBadge(
                    text = character.status,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(AppTheme.size.small)
                )

                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primaryLight,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppTheme.size.small)
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.size.normal))

            Text(
                text = character.name,
                color = AppTheme.colorScheme.primaryLight,
                style = AppTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = AppTheme.size.small)
            )

            Spacer(modifier = Modifier.height(AppTheme.size.small))

            Text(
                text = character.role,
                color = AppTheme.colorScheme.textSecondary,
                style = AppTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = AppTheme.size.small)
            )
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(AppTheme.size.statusBadgeHeight),
        shape = AppTheme.shape.button,
        color = AppTheme.colorScheme.darkCardBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppTheme.size.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.size.small)
                    .background(
                        color = AppTheme.colorScheme.accent,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.size(AppTheme.size.small))

            Text(
                text = text,
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.labelSmall
            )
        }
    }
}