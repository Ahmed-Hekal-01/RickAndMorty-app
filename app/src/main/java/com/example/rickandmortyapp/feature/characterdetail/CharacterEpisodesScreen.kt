package com.example.rickandmortyapp.feature.characterdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.Character
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.data.model.Episode
import com.example.rickandmortyapp.data.model.color
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme

@Composable
fun CharacterEpisodesScreen(
    viewModel: CharacterEpisodesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onShowSnackbar: suspend (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterEpisodesEffect.NavigateBack -> onNavigateBack()
                is CharacterEpisodesEffect.ShowError -> onShowSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                text = if (state.characterName.isNotEmpty()) state.characterName.uppercase() else "EPISODES",
                showBackButton = true,
                onBackClick = { viewModel.onEvent(CharacterEpisodesEvent.NavigateBack) }
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
                            onClick = { viewModel.onEvent(CharacterEpisodesEvent.Retry) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }

                else -> {
                    MainScreen(
                        character = state.character,
                        episodes = state.episodes
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    character: Character?,
    episodes: List<Episode>
) {
    Column(
        Modifier.padding(16.dp)
    ) {

        if (!episodes.isEmpty()) {
            Text(
                text = "Episodes (${episodes.size})",
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colorScheme.textPrimary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Spacer(Modifier.height(AppTheme.size.medium))
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (episodes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No episodes available",
                            style = AppTheme.typography.paragraph,
                            color = AppTheme.colorScheme.textSecondary
                        )
                    }
                }
            } else {
                items(episodes, key = { it.id }) { episode ->
                    EpisodeItem(episode = episode)
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .navigationBarsPadding()
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
