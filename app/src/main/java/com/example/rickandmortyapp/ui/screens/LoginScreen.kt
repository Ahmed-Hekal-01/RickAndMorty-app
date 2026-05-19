package com.example.rickandmortyapp.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.feature.auth.login.LoginEffect
import com.example.rickandmortyapp.feature.auth.login.LoginEvent
import com.example.rickandmortyapp.feature.auth.login.LoginState
import com.example.rickandmortyapp.feature.auth.login.LoginViewModel
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

@Preview(showBackground = true , showSystemUi = true)
@Composable
private fun StatefulLoginPreview() {
    var state by remember { mutableStateOf(LoginState()) }
    AppTheme() {
        LoginContent(
            state = state,
            onEvent = { event ->
                when (event) {
                    is LoginEvent.EmailChanged -> state = state.copy(email = event.email)
                    is LoginEvent.PasswordChanged -> state = state.copy(password = event.password)
                    else -> {}
                }
            }
        )

    }
}

@Preview(
    name = "Dark Mode",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun LoginScreenDarkPreview() {
    AppTheme {
        StatefulLoginPreview()
    }
}

@Preview(
    name = "Light Mode",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
private fun LoginScreenLightPreview() {
    AppTheme {
        StatefulLoginPreview()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgetPassword: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> onNavigateToHome()
                is LoginEffect.NavigateToRegister -> onNavigateToRegister()
                is LoginEffect.NavigateToSignUp -> onNavigateToSignUp()
                is LoginEffect.NavigateToForgetPassword -> onNavigateToForgetPassword()
                is LoginEffect.ShowError -> onShowSnackbar(effect.message)
                is LoginEffect.LaunchGoogleSignIn -> {

                }
            }
        }
    }
    LoginContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isImeVisible = WindowInsets.isImeVisible
    val scrollState = rememberScrollState()
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isImeVisible,
                enter = fadeIn(animationSpec = tween(100)) +
                        expandVertically(
                            animationSpec = tween(100),
                            expandFrom = Alignment.Top
                        ),
                exit = fadeOut(animationSpec = tween(100)) +
                        shrinkVertically(
                            animationSpec = tween(100),
                            shrinkTowards = Alignment.Top
                        )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                color = AppTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Rick and Morty Logo",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.welcome_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = {
                        onEvent(LoginEvent.EmailChanged(it))
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = !state.isLoading,
                    isError = state.emailError != null
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = {
                        onEvent(LoginEvent.PasswordChanged(it))
                    },
                    label = { Text("password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    isError = state.passwordError != null
                )
                TextButton(
                    onClick = { onEvent(LoginEvent.ForgetPasswordClicked) },
                    modifier = Modifier
                        .align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = AppTheme.colorScheme.accent,
                        style = AppTheme.typography.labelNormal,
                    )
                }
                OutlinedButton(
                    onClick = {
                        onEvent(LoginEvent.LoginClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    enabled = state.email.trim().isNotBlank() && state.password.trim()
                        .isNotBlank() && !state.isLoading,
                    shape = AppTheme.shape.button,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onEvent(LoginEvent.GoogleLoginClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    enabled = !state.isLoading,
                    shape = AppTheme.shape.button
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Have no account? ")

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
                                onEvent(LoginEvent.SignUpClicked)
                            }
                        ) {
                            append("SignUp")
                        }
                    },
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.labelNormal,
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }

}

@Preview
@Composable
fun OldOne() {
    AppTheme() {
        Login()
    }
}

@Composable
fun Login() {

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
                    painter = painterResource(id = R.drawable.app_logo),
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