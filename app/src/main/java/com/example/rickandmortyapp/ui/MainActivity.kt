package com.example.rickandmortyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.feature.search.SearchScreen
import com.example.rickandmortyapp.feature.splash.Destination
import com.example.rickandmortyapp.feature.splash.SplashViewModel
import com.example.rickandmortyapp.ui.navigation.AppRoot
import com.example.rickandmortyapp.ui.theme.AppTheme
import com.example.rickandmortyapp.util.AppGraphs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.rickandmortyapp.data.model.AppSettings
import com.example.rickandmortyapp.data.repository.ISettingsRepository
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewmodel: SplashViewModel by viewModels<SplashViewModel>()
    @Inject
    lateinit var settingsRepository: ISettingsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewmodel.state.value.isLoading
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.4f, 0f)
                val zoomY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.4f, 0f)

                AnimatorSet().apply {
                    playTogether(zoomX, zoomY)
                    duration = 100L
                    interpolator = FastOutLinearInInterpolator()
                    doOnEnd { screen.remove() }
                    start()
                }
            }


        }
        enableEdgeToEdge()
        setContent {
            val appSettings by settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )

            LaunchedEffect(appSettings.language) {
                applyLocale(appSettings.language)
            }

            val locale = Locale(appSettings.language)
            val configuration = android.content.res.Configuration(androidx.compose.ui.platform.LocalConfiguration.current)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            
            val layoutDirection = if (locale.language == "ar") {
                androidx.compose.ui.unit.LayoutDirection.Rtl
            } else {
                androidx.compose.ui.unit.LayoutDirection.Ltr
            }

            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalConfiguration provides configuration,
                androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection
            ) {
                AppTheme(isDarkTheme = appSettings.darkMode) {
                    val state by viewmodel.state.collectAsStateWithLifecycle()
                    val destination = state.destination

                    if (destination != null) {
                        val startDestination = when (destination) {
                            Destination.HOME -> AppGraphs.MAIN
                            Destination.LOGIN -> AppGraphs.AUTH
                        }

                        AppRoot(startDestination = startDestination)
                    }
                }
            }
        }
    }

    private fun applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        
        val appContext = applicationContext
        val appConfig = appContext.resources.configuration
        appConfig.setLocale(locale)
        appConfig.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        appContext.resources.updateConfiguration(appConfig, appContext.resources.displayMetrics)
    }
}