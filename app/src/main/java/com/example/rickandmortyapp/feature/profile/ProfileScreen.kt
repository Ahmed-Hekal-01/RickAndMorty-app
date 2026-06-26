package com.example.rickandmortyapp.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                is ProfileEffect.ShowError -> onShowSnackbar(effect.message)
                is ProfileEffect.ShowSuccess -> onShowSnackbar(effect.message)
            }
        }
    }

    ProfileContent(
        state = state,
        onLogoutClick = { viewModel.onEvent(ProfileEvent.Logout) },
        onRetryClick = { viewModel.onEvent(ProfileEvent.LoadProfile) }
    )
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onRetryClick: () -> Unit
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
            CustomTopBar("PROFILE")

            Spacer(modifier = Modifier.height(AppTheme.size.large))

            when {
                state.isLoading -> {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator(color = AppTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.weight(1f))
                }
                state.profile != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppTheme.shape.container,
                        colors = CardDefaults.cardColors(
                            containerColor = AppTheme.colorScheme.cardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(AppTheme.size.large),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.size.small)
                        ) {
                            Text(
                                text = state.profile.displayName ?: "Anonymous",
                                color = AppTheme.colorScheme.primaryLight,
                                style = AppTheme.typography.titleNormal,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.profile.email,
                                color = AppTheme.colorScheme.textSecondary,
                                style = AppTheme.typography.paragraph
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.size.large))

                    Button(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.size.buttonHeight),
                        shape = AppTheme.shape.button
                    ) {
                        Text(
                            text = "Logout",
                            style = AppTheme.typography.labelLarge
                        )
                    }
                }
                else -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = state.error ?: "Failed to load profile.",
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.paragraph
                    )
                    Spacer(modifier = Modifier.height(AppTheme.size.medium))
                    Button(
                        onClick = onRetryClick,
                        shape = AppTheme.shape.button
                    ) {
                        Text(
                            text = "Retry",
                            style = AppTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
