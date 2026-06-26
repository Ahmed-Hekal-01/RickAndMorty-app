package com.example.rickandmortyapp.feature.episodes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme
import com.example.rickandmortyapp.util.AppRoutes

@Preview(showSystemUi = true)
@Composable
fun EpisodesScreenPreview() {
    AppTheme {
        EpisodesScreenContent(
            state = EpisodesState(isLoading = false),
            onEvent = {},
            onNavClick = {}
        )
    }
}

@Composable
fun EpisodesScreen(
    viewModel: EpisodesViewModel = hiltViewModel(),
    onNavClick: (String) -> Unit = {},
    onShowSnackbar: suspend (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EpisodesEffect.ShowError -> onShowSnackbar(effect.message)
            }
        }
    }

    EpisodesScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavClick = onNavClick
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EpisodesScreenContent(
    state: EpisodesState,
    onEvent: (EpisodesEvent) -> Unit,
    onNavClick: (String) -> Unit
) {
    var selectedSeason by remember { mutableStateOf("All Episodes") }

    // Dynamic list of seasons computed from episodes
    val seasons = remember(state.episodes) {
        listOf("All Episodes") + state.episodes
            .map { "Season ${it.seasonNumber}" }
            .distinct()
            .sorted()
    }

    // Filtered episodes based on season selection
    val filteredEpisodes = remember(state.episodes, selectedSeason) {
        if (selectedSeason == "All Episodes") {
            state.episodes
        } else {
            val seasonNum = selectedSeason.substringAfter("Season ").toIntOrNull() ?: 1
            state.episodes.filter { it.seasonNumber == seasonNum }
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
                .padding(horizontal = AppTheme.size.large)
        ) {
            CustomTopBar("RICK & MORTY")

            Column(modifier = Modifier.padding(vertical = AppTheme.size.medium)) {
                Text(
                    text = "Season Guides",
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Episodes",
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.size.normal))

            SeasonChips(
                seasons = seasons,
                selectedSeason = selectedSeason,
                onSeasonClick = { selectedSeason = it }
            )

            Spacer(modifier = Modifier.height(AppTheme.size.large))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colorScheme.primary)
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error,
                            color = AppTheme.colorScheme.textPrimary,
                            style = AppTheme.typography.paragraph
                        )
                        Spacer(modifier = Modifier.height(AppTheme.size.medium))
                        Button(
                            onClick = { onEvent(EpisodesEvent.Retry) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = AppTheme.size.large
                        )
                    ) {
                        if (filteredEpisodes.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No episodes found.",
                                        style = AppTheme.typography.paragraph,
                                        color = AppTheme.colorScheme.textSecondary
                                    )
                                }
                            }
                        } else {
                            items(filteredEpisodes, key = { it.id }) { episode ->
                                EpisodeItem(episode = episode)
                                Spacer(modifier = Modifier.height(AppTheme.size.large))
                            }
                        }

                        if (state.hasMorePages) {
                            item {
                                Spacer(modifier = Modifier.height(AppTheme.size.large))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (state.isLoadingMore) {
                                        CircularProgressIndicator(color = AppTheme.colorScheme.primary)
                                    } else {
                                        OutlinedButton(
                                            onClick = { onEvent(EpisodesEvent.LoadNextPage) },
                                            modifier = Modifier
                                                .width(AppTheme.size.loadMoreWidth)
                                                .height(AppTheme.size.buttonHeight),
                                            shape = AppTheme.shape.button,
                                            border = BorderStroke(
                                                width = AppTheme.size.small,
                                                brush = Brush.horizontalGradient(
                                                    listOf(
                                                        AppTheme.colorScheme.gradientStart,
                                                        AppTheme.colorScheme.gradientEnd
                                                    )
                                                )
                                            )
                                        ) {
                                            Text(
                                                text = "LOAD MORE FILES",
                                                color = AppTheme.colorScheme.primary,
                                                style = AppTheme.typography.labelLarge
                                            )
                                            Spacer(modifier = Modifier.width(AppTheme.size.small))
                                            Icon(
                                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = AppTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeasonChips(
    seasons: List<String>,
    selectedSeason: String,
    onSeasonClick: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.size.normal),
        verticalArrangement = Arrangement.spacedBy(AppTheme.size.normal)
    ) {
        seasons.forEach { season ->
            val selected = season == selectedSeason

            Button(
                onClick = { onSeasonClick(season) },
                modifier = Modifier.height(AppTheme.size.episodeChipHeight),
                shape = AppTheme.shape.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) AppTheme.colorScheme.primaryLight else AppTheme.colorScheme.darkCardBackground,
                    contentColor = if (selected) AppTheme.colorScheme.primary else AppTheme.colorScheme.textPrimary
                ),
                contentPadding = PaddingValues(horizontal = AppTheme.size.large)
            ) {
                Text(
                    text = season,
                    style = AppTheme.typography.labelNormal
                )
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: Episode) {
    Card(
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(containerColor = AppTheme.colorScheme.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "S%02dE%02d".format(episode.seasonNumber, episode.episodeNumber),
                    style = AppTheme.typography.labelNormal,
                    color = AppTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = episode.name,
                style = AppTheme.typography.titleNormal,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.textSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = episode.airDate,
                    style = AppTheme.typography.paragraph,
                    color = AppTheme.colorScheme.textSecondary
                )
            }
        }
    }
}