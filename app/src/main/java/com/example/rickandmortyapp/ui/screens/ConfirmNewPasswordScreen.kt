package com.example.rickandmortyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun ConfirmNewPasswordScreenPreview() {
    AppTheme {
        ConfirmNewPasswordScreen()
    }
}

@Composable
fun ConfirmNewPasswordScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        Box(
            modifier = Modifier
                .size(
                    width = AppTheme.size.glowWidth,
                    height = AppTheme.size.glowHeight
                )
                .offset(
                    x = (-90).dp,
                    y = (-80).dp
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppTheme.colorScheme.glowTop.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
                .blur(AppTheme.size.glowBlur)
        )

        Box(
            modifier = Modifier
                .size(
                    width = AppTheme.size.glowWidth,
                    height = AppTheme.size.glowHeight
                )
                .align(Alignment.BottomEnd)
                .offset(
                    x = 90.dp,
                    y = 80.dp
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppTheme.colorScheme.glowBottom.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
                .blur(AppTheme.size.glowBlur)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large)
                .padding(top = 42.dp)
        ) {

            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues()
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(AppTheme.size.small))

                Text(
                    text = "Back to Login",
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelNormal
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            AnimatedGradientText(
                text = "New\nPassword",
                fontSize = 54.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Enter your new password and confirm it.",
                color = AppTheme.colorScheme.textSecondary,
                style = AppTheme.typography.paragraph
            )

            Spacer(modifier = Modifier.height(66.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.shape.container,
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colorScheme.surfaceContainer
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = AppTheme.size.large,
                        vertical = 40.dp
                    )
                ) {

                    PasswordField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        passwordVisible = passwordVisible,
                        onVisibilityClick = {
                            passwordVisible = !passwordVisible
                        }
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.large))

                    PasswordField(
                        label = "Confirm new Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        passwordVisible = passwordVisible,
                        onVisibilityClick = {
                            passwordVisible = !passwordVisible
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onContinueClick,
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
                                    text = "Continue",
                                    color = AppTheme.colorScheme.onPrimary,
                                    style = AppTheme.typography.labelLarge
                                )

                                Spacer(
                                    modifier = Modifier.width(
                                        AppTheme.size.medium
                                    )
                                )

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

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onVisibilityClick: () -> Unit
) {

    Column {

        Text(
            text = label,
            color = AppTheme.colorScheme.textPrimary,
            style = AppTheme.typography.labelNormal
        )

        Spacer(modifier = Modifier.height(AppTheme.size.small))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,

            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.size.fieldHeight),

            placeholder = {
                Text(
                    text = "••••••••",
                    color = AppTheme.colorScheme.textMuted
                )
            },

            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary
                )
            },

            trailingIcon = {

                IconButton(
                    onClick = onVisibilityClick
                ) {

                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = AppTheme.colorScheme.primary
                    )
                }
            },

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),

            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

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
    }
}