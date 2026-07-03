package com.example.rickandmortyapp.feature.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.theme.AppTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

private data class ProfileAvatar(
    val uri: String,
    @DrawableRes val drawableRes: Int
)

private val profileAvatars = listOf(
    ProfileAvatar("app://avatar/avatar_1", R.drawable.avatar_1),
    ProfileAvatar("app://avatar/avatar_2", R.drawable.avatar_2),
    ProfileAvatar("app://avatar/avatar_3", R.drawable.avatar_3),
    ProfileAvatar("app://avatar/avatar_4", R.drawable.avatar_4),
    ProfileAvatar("app://avatar/avatar_5", R.drawable.avatar_5)
)

private fun avatarDrawableFromUri(uri: String?): Int {
    return profileAvatars.firstOrNull { it.uri == uri }?.drawableRes
        ?: R.drawable.avatar_1
}

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
        onRetryClick = { viewModel.onEvent(ProfileEvent.LoadProfile) },
        onAvatarSelected = { avatarUri ->
            viewModel.onEvent(ProfileEvent.UpdateAvatar(avatarUri))
        },
        onThemeToggle = { enabled ->
            viewModel.onEvent(ProfileEvent.ToggleDarkMode(enabled))
        },
        onBioSave = { bio ->
            viewModel.onEvent(ProfileEvent.UpdateBio(bio))
        }
    )
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onBioSave: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppTheme.colorScheme.primary
                )
            }

            state.profile != null -> {
                ProfileLoadedContent(
                    state = state,
                    onLogoutClick = onLogoutClick,
                    onAvatarSelected = onAvatarSelected,
                    onThemeToggle = onThemeToggle,
                    onBioSave = onBioSave
                )
            }

            else -> {
                ProfileErrorContent(
                    message = state.error ?: "Failed to load profile.",
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@Composable
private fun ProfileLoadedContent(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onAvatarSelected: (String) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onBioSave: (String) -> Unit
) {
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showBioDialog by remember { mutableStateOf(false) }

    val profile = state.profile
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() } ?: "Anonymous"
    val email = profile?.email.orEmpty()
    val selectedAvatarUri = profile?.photoUrl
    val selectedAvatarDrawable = avatarDrawableFromUri(selectedAvatarUri)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PROFILE",
            color = AppTheme.colorScheme.primaryLight,
            style = AppTheme.typography.titleNormal,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = painterResource(id = selectedAvatarDrawable),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = AppTheme.colorScheme.border,
                        shape = CircleShape
                    )
                    .clickable { showAvatarDialog = true },
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                AppTheme.colorScheme.gradientStart,
                                AppTheme.colorScheme.neonAccent
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = AppTheme.colorScheme.screenBackground,
                        shape = CircleShape
                    )
                    .clickable { showAvatarDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Change avatar",
                    tint = AppTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = displayName,
            color = AppTheme.colorScheme.textPrimary,
            style = AppTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = email,
            color = AppTheme.colorScheme.textSecondary,
            style = AppTheme.typography.labelNormal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(44.dp))

        BioCard(
            bio = profile?.bio.orEmpty(),
            onEditClick = {
                showBioDialog = true
            }
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "APP PREFERENCES",
            color = AppTheme.colorScheme.primaryLight,
            style = AppTheme.typography.labelNormal,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PreferenceCard(
            isDarkMode = state.isDarkMode,
            onThemeToggle = onThemeToggle
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = AppTheme.shape.button,
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFF8E5B6A)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = AppTheme.colorScheme.primaryLight
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = Color(0xFFFFC6C6),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "SIGN OUT",
                    color = Color(0xFFFFC6C6),
                    style = AppTheme.typography.labelLarge
                )
            }
        }
    }

    if (showAvatarDialog) {
        AvatarPickerDialog(
            selectedAvatarUri = selectedAvatarUri,
            onDismiss = { showAvatarDialog = false },
            onAvatarSelected = { avatar ->
                showAvatarDialog = false
                onAvatarSelected(avatar.uri)
            }
        )
    }
    if (showBioDialog) {
        EditBioDialog(
            currentBio = profile?.bio.orEmpty(),
            onDismiss = {
                showBioDialog = false
            },
            onSave = { bio ->
                showBioDialog = false
                onBioSave(bio)
            }
        )
    }
}

@Composable
private fun BioCard(
    bio: String,
    onEditClick: () -> Unit
) {
    val displayedBio = bio.takeIf { it.isNotBlank() }
        ?: "Tap here to add your bio."

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BIO / DIRECTIVE",
                        color = AppTheme.colorScheme.primaryLight,
                        style = AppTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit bio",
                        tint = AppTheme.colorScheme.neonAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = displayedBio,
                    color = if (bio.isBlank()) {
                        AppTheme.colorScheme.textMuted
                    } else {
                        AppTheme.colorScheme.textSecondary
                    },
                    style = AppTheme.typography.paragraph
                )
            }

            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = null,
                tint = AppTheme.colorScheme.neonAccent.copy(alpha = 0.25f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp)
            )
        }
    }
}
@Composable
private fun EditBioDialog(
    currentBio: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var bioInput by remember(currentBio) {
        mutableStateOf(currentBio)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colorScheme.surface,
        shape = AppTheme.shape.container,
        title = {
            Text(
                text = "Edit Bio",
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.titleNormal
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { value ->
                        if (value.length <= 160) {
                            bioInput = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Write something about yourself...",
                            color = AppTheme.colorScheme.textMuted
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppTheme.colorScheme.textPrimary,
                        unfocusedTextColor = AppTheme.colorScheme.textPrimary,
                        focusedBorderColor = AppTheme.colorScheme.primaryLight,
                        unfocusedBorderColor = AppTheme.colorScheme.border,
                        cursorColor = AppTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${bioInput.length}/160",
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(bioInput)
                }
            ) {
                Text(
                    text = "Save",
                    color = AppTheme.colorScheme.primaryLight
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = AppTheme.colorScheme.textSecondary
                )
            }
        }
    )
}

@Composable
private fun PreferenceCard(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onThemeToggle(!isDarkMode)
            },
        shape = AppTheme.shape.button,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primary.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primaryLight
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Atmospheric Mode",
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isDarkMode) {
                        "Deep cosmic palette"
                    } else {
                        "Bright portal palette"
                    },
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.labelSmall
                )
            }

            Switch(
                checked = isDarkMode,
                onCheckedChange = onThemeToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppTheme.colorScheme.onPrimary,
                    checkedTrackColor = AppTheme.colorScheme.primary,
                    uncheckedThumbColor = AppTheme.colorScheme.textMuted,
                    uncheckedTrackColor = AppTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ProfileErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = AppTheme.colorScheme.textSecondary,
            style = AppTheme.typography.paragraph,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onRetryClick,
            shape = AppTheme.shape.button
        ) {
            Text(
                text = "Retry",
                style = AppTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AvatarPickerDialog(
    selectedAvatarUri: String?,
    onDismiss: () -> Unit,
    onAvatarSelected: (ProfileAvatar) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colorScheme.surface,
        shape = AppTheme.shape.container,
        title = {
            Text(
                text = "Choose Profile Image",
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.titleNormal
            )
        },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                profileAvatars.forEach { avatar ->
                    val isSelected = avatar.uri == selectedAvatarUri

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) {
                                    AppTheme.colorScheme.neonAccent
                                } else {
                                    AppTheme.colorScheme.border
                                },
                                shape = CircleShape
                            )
                            .clickable {
                                onAvatarSelected(avatar)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = avatar.drawableRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(66.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AppTheme.colorScheme.neonAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    color = AppTheme.colorScheme.primaryLight
                )
            }
        }
    )
}