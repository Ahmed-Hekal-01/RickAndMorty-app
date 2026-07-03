package com.example.rickandmortyapp.feature.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.util.AppGraphs
import com.example.rickandmortyapp.util.AppRoutes

/*@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    AppTheme {
        RegistrationScreen(
            onNavigateToHome = {
            navController.navigate(AppGraphs.MAIN) {
                popUpTo(AppGraphs.AUTH) { inclusive = true }
            }
        },
            onNavigateToLogin = {
                navController.navigate(AppRoutes.LOGIN_SCREEN) {
                    popUpTo(AppRoutes.SIGN_UP_SCREEN) { inclusive = true }
                }
            },
            onShowSnackbar = { message ->
                showSnackbar(message)
            })
    }
}*/

@Composable
fun RegistrationScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val googleAuth = remember(context) { GoogleAuthUiClient(context) }
    val scrollState = rememberScrollState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToHome -> onNavigateToHome()

                is RegisterEffect.NavigateToLogin -> onNavigateToLogin()

                is RegisterEffect.ShowError -> onShowSnackbar(effect.message)

                is RegisterEffect.LaunchGoogleSignIn -> {
                    when (val result = googleAuth.fetchGoogleIdToken()) {
                        is NetworkResult.Success -> {
                            viewModel.onEvent(
                                RegisterEvent.GoogleTokenReceived(result.data)
                            )
                        }

                        is NetworkResult.Error.UserCancellation -> Unit

                        else -> {
                            onShowSnackbar("Google sign-up failed. Please try again.")
                        }
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val isSmallHeight = maxHeight < 760.dp

        val outerPadding = if (isSmallHeight) 12.dp else AppTheme.size.medium
        val cardInnerPadding = if (isSmallHeight) 16.dp else 24.dp
        val logoTopSpace = if (isSmallHeight) 8.dp else 20.dp
        val afterLogoSpace = if (isSmallHeight) 24.dp else 40.dp
        val formSpace = if (isSmallHeight) 14.dp else 20.dp
        val buttonTopSpace = if (isSmallHeight) 28.dp else 40.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(outerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = AppTheme.shape.container,
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isSmallHeight) 12.dp else AppTheme.size.medium)
                        .border(
                            width = 1.dp,
                            color = AppTheme.colorScheme.border,
                            shape = AppTheme.shape.container
                        )
                        .padding(cardInnerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(logoTopSpace))

                    AnimatedGradientText(
                        text = "RICK & MORTY",
                        fontSize = if (isSmallHeight) 20.sp else 22.sp
                    )

                    Spacer(modifier = Modifier.height(afterLogoSpace))

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

                    Spacer(modifier = Modifier.height(if (isSmallHeight) 24.dp else 32.dp))

                    CustomTextField(
                        label = "Full Name",
                        value = state.fullName,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.FullNameChanged(it))
                        },
                        placeholder = "Jane Doe",
                        isError = state.fullNameError != null,
                        errorText = state.fullNameError,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primaryLight
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(formSpace))

                    CustomTextField(
                        label = "Email",
                        value = state.email,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.EmailChanged(it))
                        },
                        placeholder = "jane@example.com",
                        keyboardType = KeyboardType.Email,
                        isError = state.emailError != null,
                        errorText = state.emailError,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primaryLight
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(formSpace))

                    CustomTextField(
                        label = "Password",
                        value = state.password,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.PasswordChanged(it))
                        },
                        placeholder = "••••••••",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = {
                            passwordVisible = !passwordVisible
                        },
                        isError = state.passwordError != null,
                        errorText = state.passwordError,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primaryLight
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(formSpace))

                    CustomTextField(
                        label = "Confirm Password",
                        value = state.confirmPassword,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it))
                        },
                        placeholder = "••••••••",
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onPasswordVisibilityChange = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        },
                        isError = state.confirmPasswordError != null,
                        errorText = state.confirmPasswordError,
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = AppTheme.colorScheme.primaryLight
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(buttonTopSpace))

                    Button(
                        onClick = {
                            viewModel.onEvent(RegisterEvent.RegisterClicked)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
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
                                    color = AppTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "CREATE ACCOUNT",
                                    color = AppTheme.colorScheme.onPrimary,
                                    style = AppTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isSmallHeight) 18.dp else 24.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(RegisterEvent.GoogleRegisterClicked)
                        },
                        enabled = !state.isLoading && !state.isGoogleLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
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
                        if (state.isGoogleLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = AppTheme.colorScheme.primary
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp),
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "CONTINUE WITH GOOGLE",
                                color = AppTheme.colorScheme.primary,
                                style = AppTheme.typography.labelNormal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isSmallHeight) 20.dp else 28.dp))

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
                                    viewModel.onEvent(RegisterEvent.NavigateToLogin)
                                }
                            ) {
                                append("Sign in here")
                            }
                        },
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.labelNormal
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
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
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null
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
                .defaultMinSize(minHeight = 56.dp)
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
                            imageVector = Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = AppTheme.colorScheme.primaryLight
                        )
                    }
                }
            },
            visualTransformation =
                if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            singleLine = true,
            isError = isError,
            supportingText = {
                errorText?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            shape = AppTheme.shape.button,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = AppTheme.colorScheme.surfaceVariant,
                errorContainerColor = AppTheme.colorScheme.surfaceVariant,

                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = MaterialTheme.colorScheme.error,

                focusedTextColor = AppTheme.colorScheme.textPrimary,
                unfocusedTextColor = AppTheme.colorScheme.textPrimary,
                errorTextColor = AppTheme.colorScheme.textPrimary,

                cursorColor = AppTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            )
        )
    }
}