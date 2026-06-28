package com.example.rickandmortyapp.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun OtpVerificationScreenPreview() {
    AppTheme {
        OtpVerificationScreen()
    }
}

@Composable
fun OtpVerificationScreen(
    onBackClick: () -> Unit = {},
    onAuthenticateClick: () -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
            .padding(horizontal = AppTheme.size.large)
    ) {

        Row(
            modifier = Modifier
                .padding(top = AppTheme.size.large)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = AppTheme.colorScheme.textPrimary
                )
            }

            Text(
                text = "BACK",
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.labelNormal
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 170.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedGradientText(
                text = "Verify Identity",
                fontSize = AppTheme.typography.titleLarge.fontSize
            )

            Spacer(modifier = Modifier.height(54.dp))

            Text(
                text = "Enter the 6-digit cosmic key transmitted to\nyour device.",
                color = AppTheme.colorScheme.textSecondary,
                style = AppTheme.typography.paragraph,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(78.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = AppTheme.size.large,
                        shape = AppTheme.shape.container
                    ),
                shape = AppTheme.shape.container,
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = AppTheme.size.large,
                        vertical = AppTheme.size.large
                    )
                ) {

                    OtpInputField(
                        otp = otp,
                        onOtpChange = { value ->
                            if (value.length <= 6 && value.all { it.isDigit() }) {
                                otp = value
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.large))

                    Text(
                        text = "60 Seconds",
                        color = AppTheme.colorScheme.textPrimary,
                        style = AppTheme.typography.labelNormal
                    )

                    Spacer(modifier = Modifier.height(AppTheme.size.normal))

                    Button(
                        onClick = onAuthenticateClick,
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
                                    text = "AUTHENTICATE",
                                    color = AppTheme.colorScheme.onPrimary,
                                    style = AppTheme.typography.labelLarge
                                )

                                Spacer(modifier = Modifier.width(AppTheme.size.small))

                                Icon(
                                    imageVector = Icons.Outlined.Fingerprint,
                                    contentDescription = null,
                                    tint = AppTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.size.large))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn’t receive a signal?",
                            color = AppTheme.colorScheme.textPrimary,
                            style = AppTheme.typography.labelNormal
                        )

                        TextButton(
                            onClick = onResendClick,
                            contentPadding = PaddingValues()
                        ) {
                            Text(
                                text = "↻ Resend Code",
                                color = AppTheme.colorScheme.accent,
                                style = AppTheme.typography.labelNormal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = onOtpChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        textStyle = TextStyle(
            color = Color.Transparent
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""

                    Box(
                        modifier = Modifier
                            .size(
                                width = AppTheme.size.otpBoxWidth,
                                height = AppTheme.size.otpBoxHeight
                            )
                            .background(
                                color = AppTheme.colorScheme.inputField,
                                shape = AppTheme.shape.button
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = AppTheme.colorScheme.textSecondary,
                            style = AppTheme.typography.titleNormal
                        )
                    }
                }
            }
        }
    )
}