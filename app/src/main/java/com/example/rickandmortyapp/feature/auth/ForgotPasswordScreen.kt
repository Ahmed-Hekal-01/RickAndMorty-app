package com.example.rickandmortyapp.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    onBackClick: () -> Unit = {},
    onSendResetLinkClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

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
                onClick = onBackClick,
                contentPadding = PaddingValues()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = AppTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.width(AppTheme.size.small))

                Text(
                    text = "Back to Login",
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelNormal
                )
            }

            Spacer(modifier  = Modifier.height(AppTheme.size.titleToTopSpacing))

            AnimatedGradientText(
                text = "Reset",
                fontSize = AppTheme.size.titleFontSize
            )

            Spacer(modifier = Modifier.height(AppTheme.size.medium))

            Text(
                text = "Enter the email associated with your\naccount and we'll send a link to reset\nyour password.",
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
                        text = "Email Address",
                        color = AppTheme.colorScheme.textPrimary,
                        style = AppTheme.typography.labelNormal
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.small))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.size.fieldHeight),
                        placeholder = {
                            Text(
                                text = "your@email.com",
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
                        onClick = onSendResetLinkClick,
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Send Reset Link",
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