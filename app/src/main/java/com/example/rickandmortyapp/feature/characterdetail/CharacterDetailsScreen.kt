package com.example.rickandmortyapp.feature.characterdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.components.BottomNavBar
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun CharacterDetailsScreenPreview() {
    AppTheme {
        CharacterDetailsScreen()
    }
}

@Composable
fun CharacterDetailsScreen(
    name: String = "Kaelen Voss",
    species: String = "Humanoid",
    gender: String = "Male",
    origin: String = "Earth (C-137)",
    location: String = "Citadel of Ricks",
    episodes: String = "42 Episodes",
    image: Int = R.drawable.app_logo,
    onNavClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            CustomTopBar("RICK & MORTY")

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = AppTheme.size.bottomBarHeight + AppTheme.size.large
                )
            ) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.size.detailsHeroHeight)
                    ) {

                        Image(
                            painter = painterResource(id = image),
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            AppTheme.colorScheme.screenBackground.copy(alpha = 0f),
                                            AppTheme.colorScheme.screenBackground.copy(alpha = 0.25f),
                                            AppTheme.colorScheme.screenBackground
                                        )
                                    )
                                )
                        )

                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(AppTheme.size.large)
                        )

                        StatusBadge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(AppTheme.size.large)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = AppTheme.size.large)
                                .padding(bottom = AppTheme.size.medium)
                        ) {

                            Text(
                                text = "SUBJECT DESIGNATION",
                                color = AppTheme.colorScheme.textSecondary,
                                style = AppTheme.typography.labelSmall
                            )

                            AnimatedGradientText(
                                text = name,
                                fontSize = AppTheme.typography.titleLarge.fontSize
                            )
                        }
                    }
                }

                item {

                    Spacer(modifier = Modifier.height(AppTheme.size.small))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.size.large)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.size.medium)
                        ) {

                            DetailSmallCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.Fingerprint,
                                label = "SPECIES",
                                value = species
                            )

                            DetailSmallCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.Male,
                                label = "GENDER",
                                value = gender
                            )
                        }

                        Spacer(modifier = Modifier.height(AppTheme.size.normal))

                        DetailLargeCard(
                            icon = Icons.Outlined.Public,
                            label = "ORIGIN",
                            value = origin
                        )

                        Spacer(modifier = Modifier.height(AppTheme.size.normal))

                        DetailLargeCard(
                            icon = Icons.Outlined.LocationOn,
                            label = "CURRENT LOCATION",
                            value = location
                        )

                        Spacer(modifier = Modifier.height(AppTheme.size.normal))

                        EpisodesPresenceCard(
                            value = episodes
                        )

                        Spacer(modifier = Modifier.height(AppTheme.size.large))
                    }
                }
            }
        }
        BottomNavBar(
            selectedRoute = "home",
            onNavigate = onNavClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatusBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = AppTheme.shape.button,
        color = AppTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.size.medium,
                vertical = AppTheme.size.small
            ),
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

            Spacer(modifier = Modifier.width(AppTheme.size.small))

            Text(
                text = "ACTIVE",
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun DetailSmallCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier.height(AppTheme.size.detailsSmallCardHeight),
        shape = AppTheme.shape.button,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.cardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.medium)
        ) {
            CircleIcon(icon = icon)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = label,
                color = AppTheme.colorScheme.primaryLight,
                style = AppTheme.typography.labelSmall
            )

            Text(
                text = value,
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun DetailLargeCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.detailsLargeCardHeight),
        shape = AppTheme.shape.button,
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
            CircleIcon(icon = icon)

            Spacer(modifier = Modifier.width(AppTheme.size.medium))

            Column {
                Text(
                    text = label,
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelSmall
                )

                Text(
                    text = value,
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun EpisodesPresenceCard(
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.detailsLargeCardHeight),
        shape = AppTheme.shape.button,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.primaryDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppTheme.colorScheme.gradientStart,
                            AppTheme.colorScheme.primaryDark
                        )
                    )
                )
                .padding(AppTheme.size.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.size.detailsIconSize)
                    .background(
                        color = AppTheme.colorScheme.primaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tv,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.size.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "PRESENCE",
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelSmall
                )

                Text(
                    text = value,
                    color = AppTheme.colorScheme.onPrimary,
                    style = AppTheme.typography.labelLarge
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = AppTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun CircleIcon(
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .size(AppTheme.size.detailsIconSize)
            .background(
                color = AppTheme.colorScheme.primaryDark.copy(alpha = 0.9f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppTheme.colorScheme.primaryLight
        )
    }
}