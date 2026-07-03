package com.example.rickandmortyapp.feature.episodes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme
import androidx.core.net.toUri

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
    var selectedSeasonNumber by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current

    val seasonNumbers = remember(state.episodes) {
        state.episodes
            .map { it.seasonNumber }
            .distinct()
            .sorted()
    }

    val filteredEpisodes = remember(state.episodes, selectedSeasonNumber) {
        if (selectedSeasonNumber == null) {
            state.episodes
        } else {
            state.episodes.filter { it.seasonNumber == selectedSeasonNumber }
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
            CustomTopBar(stringResource(R.string.home_screen_top_bar_name))


            Column(modifier = Modifier.padding(vertical = AppTheme.size.medium)) {
                Text(
                    text = stringResource(R.string.season_guides),
                    color = AppTheme.colorScheme.primaryLight,
                    style = AppTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.episodes_title),
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.size.normal))

            SeasonChips(
                seasonNumbers = seasonNumbers,
                selectedSeasonNumber = selectedSeasonNumber,
                onSeasonClick = { selectedSeasonNumber = it }
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
                            Text(stringResource(R.string.retry), color = Color.White)
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
                                        text = stringResource(R.string.no_episodes_found),
                                        style = AppTheme.typography.paragraph,
                                        color = AppTheme.colorScheme.textSecondary
                                    )
                                }
                            }
                        } else {
                            items(filteredEpisodes, key = { it.id }) { episode ->
                                EpisodeItem(
                                    episode = episode,
                                    onClick = {
                                        openEpisodeInBrowser(
                                            context = context,
                                            seasonNumber = episode.seasonNumber,
                                            episodeNumber = episode.episodeNumber
                                        )
                                    }
                                )

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
                                                text = stringResource(R.string.load_more_episodes),
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
    seasonNumbers: List<Int>,
    selectedSeasonNumber: Int?,
    onSeasonClick: (Int?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.size.normal),
        verticalArrangement = Arrangement.spacedBy(AppTheme.size.normal)
    ) {
        val isAllSelected = selectedSeasonNumber == null

        Button(
            onClick = { onSeasonClick(null) },
            modifier = Modifier.height(AppTheme.size.episodeChipHeight),
            shape = AppTheme.shape.button,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAllSelected) AppTheme.colorScheme.primaryLight else AppTheme.colorScheme.darkCardBackground,
                contentColor = if (isAllSelected) AppTheme.colorScheme.primary else AppTheme.colorScheme.textPrimary
            ),
            contentPadding = PaddingValues(horizontal = AppTheme.size.large)
        ) {
            Text(
                text = stringResource(R.string.all_episodes),
                style = AppTheme.typography.labelNormal
            )
        }

        seasonNumbers.forEach { seasonNum ->
            val selected = seasonNum == selectedSeasonNumber

            Button(
                onClick = { onSeasonClick(seasonNum) },
                modifier = Modifier.height(AppTheme.size.episodeChipHeight),
                shape = AppTheme.shape.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) AppTheme.colorScheme.primaryLight else AppTheme.colorScheme.darkCardBackground,
                    contentColor = if (selected) AppTheme.colorScheme.primary else AppTheme.colorScheme.textPrimary
                ),
                contentPadding = PaddingValues(horizontal = AppTheme.size.large)
            ) {
                Text(
                    text = stringResource(R.string.season_format, seasonNum),
                    style = AppTheme.typography.labelNormal
                )
            }
        }
    }
}

private fun arabicSeasonName(seasonNumber: Int): String {
    return when (seasonNumber) {
        1 -> "الاول"
        2 -> "الثاني"
        3 -> "الثالث"
        4 -> "الرابع"
        5 -> "الخامس"
        6 -> "السادس"
        7 -> "السابع"
        8 -> "الثامن"
        9 -> "التاسع"
        10 -> "العاشر"
        else -> seasonNumber.toString()
    }
}

private fun buildEpisodeWatchUrl(
    seasonNumber: Int,
    episodeNumber: Int
): String {
    val seasonArabicName = arabicSeasonName(seasonNumber)

    val slug =
        "مسلسل-rick-and-morty-الموسم-$seasonArabicName-الحلقة-$episodeNumber-مترجمة"

    return "https://web.topcinemaa.com/${Uri.encode(slug, "-")}/"
}

private fun openEpisodeInBrowser(
    context: android.content.Context,
    seasonNumber: Int,
    episodeNumber: Int
) {
    val url = buildEpisodeWatchUrl(
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber
    )

    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

@Composable
fun EpisodeItem(
    episode: Episode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.cardBackground
        ),
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
                    text = "S%02dE%02d".format(
                        episode.seasonNumber,
                        episode.episodeNumber
                    ),
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
