package com.example.rickandmortyapp.feature.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    AppTheme {
        ForgotPasswordScreen()
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ForgotPasswordEffect.NavigateBackToLogin -> onBackClick()
                is ForgotPasswordEffect.ShowMessage -> onShowSnackbar(effect.message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        Box(
            modifier = Modifier
                .size(AppTheme.size.glowWidth)
                .offset(x = (-90).dp, y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppTheme.colorScheme.glowTop.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(AppTheme.size.glowBlur)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large)
                .padding(top = AppTheme.size.topScreenPadding),
        ) {

            TextButton(
                onClick = {
                    viewModel.onEvent(ForgotPasswordEvent.BackToLoginClicked)
                },
                contentPadding = PaddingValues()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back_desc),
                    tint = AppTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.width(AppTheme.size.small))

                Text(
                    text = stringResource(R.string.back_to_login),
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelNormal
                )
            }

            Spacer(modifier  = Modifier.height(AppTheme.size.titleToTopSpacing))

            AnimatedGradientText(
                text = stringResource(R.string.reset_title),
                fontSize = AppTheme.size.titleFontSize
            )

            Spacer(modifier = Modifier.height(AppTheme.size.medium))

            Text(
                text = stringResource(R.string.reset_instructions),
                color = AppTheme.colorScheme.textSecondary,
                style = AppTheme.typography.paragraph,
                lineHeight  = AppTheme.size.paragraphLineHeight
            )

            Spacer(modifier = Modifier.height(96.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.shape.container,
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = AppTheme.size.medium,
                        vertical = AppTheme.size.medium
                    )
                ) {

                    Text(
                        text = stringResource(R.string.email_address),
                        color = AppTheme.colorScheme.textPrimary,
                        style = AppTheme.typography.labelNormal
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.small))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = {
                            viewModel.onEvent(ForgotPasswordEvent.EmailChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = AppTheme.size.fieldHeight),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.email_placeholder),
                                color = AppTheme.colorScheme.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        isError = state.emailError != null,
                        supportingText = {
                            state.emailError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = AppTheme.shape.button,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AppTheme.colorScheme.inputField,
                            unfocusedContainerColor = AppTheme.colorScheme.inputField,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = AppTheme.colorScheme.textPrimary,
                            unfocusedTextColor = AppTheme.colorScheme.textPrimary,
                            cursorColor = AppTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.large))

                    Button(
                        onClick = {
                            viewModel.onEvent(ForgotPasswordEvent.SendResetLinkClicked)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.size.buttonHeight),
                        shape = AppTheme.shape.button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            AppTheme.colorScheme.gradientStart,
                                            AppTheme.colorScheme.gradientEnd
                                        )
                                    ),
                                    shape = AppTheme.shape.button
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = AppTheme.colorScheme.onPrimary
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.send_reset_link),
                                        color = AppTheme.colorScheme.onPrimary,
                                        style = AppTheme.typography.labelLarge
                                    )

                                    Spacer(modifier = Modifier.width(AppTheme.size.small))

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                        contentDescription = null,
                                        tint = AppTheme.colorScheme.onPrimary
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