package com.example.rickandmortyapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen()
    }
}

@Composable
fun LoginScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(55.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AppTheme.colorScheme.glowPrimary.copy(alpha = 0.35f),
                                AppTheme.colorScheme.glowPrimary.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            radius = 480f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Rick and Morty Logo",
                    modifier = Modifier.size(240.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AnimatedGradientText(
                text = "Welcome Back",
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Authenticate to access the news.",
                style = AppTheme.typography.paragraph,
                color = AppTheme.colorScheme.textSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "Email Address",
                        color = AppTheme.colorScheme.textMuted.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = AppTheme.shape.button,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = AppTheme.colorScheme.surfaceVariant,

                    focusedTextColor = AppTheme.colorScheme.textPrimary,
                    unfocusedTextColor = AppTheme.colorScheme.textPrimary,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,

                    cursorColor = AppTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Password",
                        color = AppTheme.colorScheme.textMuted.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = AppTheme.shape.button,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = AppTheme.colorScheme.surfaceVariant,

                    focusedTextColor = AppTheme.colorScheme.textPrimary,
                    unfocusedTextColor = AppTheme.colorScheme.textPrimary,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,

                    cursorColor = AppTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Forgot Password?",
                color = AppTheme.colorScheme.accent,
                style = AppTheme.typography.labelNormal,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = AppTheme.shape.button
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
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
                        text = "INITIALIZE SESSION →",
                        color = AppTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    append("Unregistered entity? ")

                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "SIGN_UP",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = AppTheme.colorScheme.accent,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        ) {
                            println("Sign Up clicked")
                        }
                    ) {
                        append("Request Access")
                    }
                },
                color = AppTheme.colorScheme.textSecondary,
                style = AppTheme.typography.labelSmall
            )
        }
    }
}