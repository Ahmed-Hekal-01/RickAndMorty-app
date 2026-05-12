package com.example.rickandmortyapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.components.CustomBottomNavBar
import com.example.rickandmortyapp.ui.theme.AppTheme

data class EpisodeUi(
    val episodeCode: String,
    val date: String,
    val title: String,
    val description: String,
    val productionCode: String,
    val season: String
)

@Preview(showSystemUi = true)
@Composable
fun EpisodesScreenPreview() {
    AppTheme {
        EpisodesScreen()
    }
}

@Composable
fun EpisodesScreen(
    onNavClick: (String) -> Unit = {},
    onLoadMoreClick: () -> Unit = {}
) {
    var selectedSeason by remember { mutableStateOf("All Episodes") }

    val seasons = listOf(
        "All Episodes",
        "Season 1",
        "Season 2",
        "Season 3"
    )

    val episodes = listOf(
        EpisodeUi(
            episodeCode = "S01E01",
            date = "Dec 02, 2013",
            title = "Pilot",
            description = "An introduction to the eccentric universe. The transmission begins with an unexpected...",
            productionCode = "CODE: 101",
            season = "Season 1"
        ),
        EpisodeUi(
            episodeCode = "S01E02",
            date = "Dec 09, 2013",
            title = "Lawnmower Dog",
            description = "A simple device meant to increase canine intelligence leads to an unforeseen uprising,...",
            productionCode = "CODE: 102",
            season = "Season 1"
        ),
        EpisodeUi(
            episodeCode = "S01E03",
            date = "Dec 16, 2013",
            title = "Anatomy Park",
            description = "An expedition into a microscopic amusement park built within a living host. The team must...",
            productionCode = "CODE: 103",
            season = "Season 1"
        ),
        EpisodeUi(
            episodeCode = "S01E04",
            date = "Jan 13, 2014",
            title = "M. Night Shaym-Aliens!",
            description = "Trapped in a complex simulation, the subjects must decipher reality from illusion while...",
            productionCode = "CODE: 104",
            season = "Season 1"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large),
            contentPadding = PaddingValues(
                top = AppTheme.size.medium,
                bottom = AppTheme.size.bottomBarHeight + AppTheme.size.large
            )
        ) {

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedGradientText(
                        text = "RICK & MORTY",
                        fontSize = AppTheme.typography.titleNormal.fontSize
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.size.large))

                AnimatedGradientText(
                    text = "Episodes",
                    fontSize = AppTheme.typography.titleLarge.fontSize
                )

                Spacer(modifier = Modifier.height(AppTheme.size.large))

                SeasonChips(
                    seasons = seasons,
                    selectedSeason = selectedSeason,
                    onSeasonClick = { selectedSeason = it }
                )

                Spacer(modifier = Modifier.height(AppTheme.size.large))
            }

            items(episodes) { episode ->
                EpisodeCard(episode = episode)

                Spacer(modifier = Modifier.height(AppTheme.size.large))
            }

            item {
                OutlinedButton(
                    onClick = onLoadMoreClick,
                    modifier = Modifier
                        .width(AppTheme.size.loadMoreWidth)
                        .height(AppTheme.size.buttonHeight)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
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

                Spacer(modifier = Modifier.height(AppTheme.size.large))
            }
        }

        CustomBottomNavBar(
            selectedRoute = "episodes",
            onItemClick = onNavClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

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
                    containerColor =
                        if (selected)
                            AppTheme.colorScheme.primaryLight
                        else
                            AppTheme.colorScheme.darkCardBackground,
                    contentColor =
                        if (selected)
                            AppTheme.colorScheme.primary
                        else
                            AppTheme.colorScheme.textPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = AppTheme.size.large
                )
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
private fun EpisodeCard(
    episode: EpisodeUi
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.episodeCardHeight),
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.cardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.large)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.height(AppTheme.size.episodeChipHeight),
                    shape = AppTheme.shape.button,
                    color = AppTheme.colorScheme.darkCardBackground
                ) {
                    Box(
                        modifier = Modifier
                            .width(AppTheme.size.episodeCodeWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = episode.episodeCode,
                            color = AppTheme.colorScheme.accent,
                            style = AppTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppTheme.size.medium))

                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(AppTheme.size.small))

                Text(
                    text = episode.date,
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelNormal
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.size.large))

            Text(
                text = episode.title,
                color = AppTheme.colorScheme.primaryLight,
                style = AppTheme.typography.titleNormal
            )

            Spacer(modifier = Modifier.height(AppTheme.size.medium))

            Text(
                text = episode.description,
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.paragraph
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                color = AppTheme.colorScheme.divider
            )

            Spacer(modifier = Modifier.height(AppTheme.size.medium))

            Surface(
                modifier = Modifier.height(AppTheme.size.episodeChipHeight),
                shape = AppTheme.shape.button,
                color = AppTheme.colorScheme.darkCardBackground
            ) {
                Box(
                    modifier = Modifier.width(AppTheme.size.episodeCodeWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = episode.productionCode,
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}