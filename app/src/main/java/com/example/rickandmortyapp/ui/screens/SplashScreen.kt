package com.example.rickandmortyapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.feature.splash.Destination
import com.example.rickandmortyapp.feature.splash.SplashEffect
import com.example.rickandmortyapp.feature.splash.SplashState
import com.example.rickandmortyapp.feature.splash.SplashViewModel
import com.example.rickandmortyapp.ui.theme.AppTheme
import android.content.res.Configuration

import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SplashScreenContent(state = state)
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateTo -> {
                    when (effect.destination) {
                        Destination.LOGIN -> {
                            onNavigateToLogin()
                        }

                        Destination.HOME -> {
                            onNavigateToHome()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(
    state: SplashState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
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
            Spacer(modifier = Modifier.height(AppTheme.size.small))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = AppTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(60.dp))
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.6f),
                    color = AppTheme.colorScheme.primary,
                    trackColor = AppTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}

@Preview(
    name = "light Mode",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)

@Composable
fun SplashScreenContentPreviewLight() {
    AppTheme {
        SplashScreenContent(state = SplashState())
    }
}

@Preview(
    name = "Dark Mode",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun SplashScreenContentPreviewDark() {
    AppTheme {
        SplashScreenContent(state = SplashState())
    }
}
