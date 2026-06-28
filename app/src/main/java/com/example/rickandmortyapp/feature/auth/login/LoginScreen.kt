package com.example.rickandmortyapp.feature.auth.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.remote.NetworkResult
import com.example.rickandmortyapp.feature.auth.register.GoogleAuthUiClient
import com.example.rickandmortyapp.ui.theme.AppTheme


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StatefulLoginPreview() {
    var state by remember { mutableStateOf(LoginState()) }
    AppTheme {
        LoginContent(
            state = state, onEvent = { event ->
                when (event) {
                    is LoginEvent.EmailChanged -> state = state.copy(email = event.email)
                    is LoginEvent.PasswordChanged -> state = state.copy(password = event.password)
                    else -> {}
                }
            })

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
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgetPassword: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val googleAuth = remember(context) { GoogleAuthUiClient(context) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> onNavigateToHome()
                is LoginEffect.NavigateToSignUp -> onNavigateToSignUp()
                is LoginEffect.NavigateToForgetPassword -> onNavigateToForgetPassword()
                is LoginEffect.ShowError -> onShowSnackbar(effect.message)
                is LoginEffect.LaunchGoogleSignIn -> {
                    when (val result = googleAuth.fetchGoogleIdToken()) {
                        is NetworkResult.Success -> {
                            viewModel.onEvent(LoginEvent.GoogleTokenReceived(result.data))
                        }

                        is NetworkResult.Error.UserCancellation -> Unit
                        else -> {
                            onShowSnackbar("Google sign-in failed. Please try again.")
                        }
                    }
                }
            }
        }
    }
    LoginContent(
        state = state, onEvent = viewModel::onEvent
    )
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoginContent(
    state: LoginState, onEvent: (LoginEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isAnyLoading = state.isEmailLoading || state.isGoogleLoading
    val isImeVisible = WindowInsets.isImeVisible
    val scrollState = rememberScrollState()
    Scaffold { _ ->
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isImeVisible,
                enter = fadeIn(animationSpec = tween(100)) + expandVertically(
                    animationSpec = tween(100), expandFrom = Alignment.Top
                ),
                exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(
                    animationSpec = tween(100), shrinkTowards = Alignment.Top
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                color = AppTheme.colorScheme.primary, shape = CircleShape
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
                    enabled = !isAnyLoading,
                    isError = state.emailError != null
                )
                Spacer(modifier = Modifier.height(8.dp))

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
                    enabled = !isAnyLoading,
                    isError = state.passwordError != null
                )
                TextButton(
                    onClick = { onEvent(LoginEvent.ForgetPasswordClicked) },
                    modifier = Modifier.align(Alignment.End),
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
                        .isNotBlank() && !isAnyLoading,
                    shape = AppTheme.shape.button,
                ) {
                    if (state.isEmailLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Login", style = MaterialTheme.typography.titleMedium
                        )
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onEvent(LoginEvent.GoogleLoginClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    enabled = !isAnyLoading,
                    shape = AppTheme.shape.button
                ) {
                    if (state.isGoogleLoading) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "Have no account? ",
                        color = AppTheme.colorScheme.textSecondary,
                        style = AppTheme.typography.labelNormal
                    )
                    Text(
                        text = "SignUp",
                        color = AppTheme.colorScheme.accent,
                        style = AppTheme.typography.labelNormal.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier
                            .clickable {
                                println("hello from sign up")
                                onEvent(LoginEvent.SignUpClicked)
                            }
                            .padding(4.dp))
                }
            }

        }
    }

}
