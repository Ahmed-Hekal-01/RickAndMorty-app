package com.example.rickandmortyapp.feature.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    AppTheme {
        RegistrationScreen()
    }
}

@Composable
fun RegistrationScreen() {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
            .padding(AppTheme.size.medium)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = AppTheme.shape.container,
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(AppTheme.size.medium)
                    .border(
                        width = 1.dp,
                        color = AppTheme.colorScheme.border,
                        shape = AppTheme.shape.container
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedGradientText(
                    text = "RICK & MORTY",
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Create Account",
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colorScheme.primaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Join the luminous network today.",
                    style = AppTheme.typography.paragraph,
                    color = AppTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.height(40.dp))

                CustomTextField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Jane Doe",
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "jane@example.com",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = {
                        passwordVisible = !passwordVisible
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = {
                        passwordVisible = !passwordVisible
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
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

                        Text(
                            text = "CREATE ACCOUNT →",
                            color = AppTheme.colorScheme.onPrimary,
                            style = AppTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = AppTheme.shape.button,
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                AppTheme.colorScheme.gradientStart,
                                AppTheme.colorScheme.gradientEnd
                            )
                        )
                    )
                ) {

                    Text(
                        text = "G",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEA4335)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "CONTINUE WITH GOOGLE →",
                        color = AppTheme.colorScheme.primary,
                        style = AppTheme.typography.labelNormal
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = buildAnnotatedString {

                        append("Already have an account? ")

                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "LOGIN",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = AppTheme.colorScheme.accent,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            ) {

                            }
                        ) {
                            append("Sign in here")
                        }
                    },
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.labelNormal
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {},
    leadingIcon: @Composable (() -> Unit)? = null
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            color = AppTheme.colorScheme.primaryLight,
            style = AppTheme.typography.labelNormal
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shape.button),
            placeholder = {
                Text(
                    text = placeholder,
                    color = AppTheme.colorScheme.textMuted
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = onPasswordVisibilityChange
                    ) {
                        Icon(
                            Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                }
            },
            visualTransformation =
                if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            shape = AppTheme.shape.button,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = AppTheme.colorScheme.surfaceVariant,

                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,

                focusedTextColor = AppTheme.colorScheme.textPrimary,
                unfocusedTextColor = AppTheme.colorScheme.textPrimary,

                cursorColor = AppTheme.colorScheme.primary
            )
        )
    }
}