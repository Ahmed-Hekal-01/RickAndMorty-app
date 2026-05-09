package com.example.rickandmortyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

private val darkColorScheme = AppColorScheme(
    // add the color scheme for the dark mode
    background = TODO(),
    onBackground = TODO(),
    primary = TODO(),
    onPrimary = TODO(),
    secondary = TODO(),
    onSecondary = TODO()
)

private val lightColorScheme = AppColorScheme(
    // add the color scheme for the light mode
    background = TODO(),
    onBackground = TODO(),
    primary = TODO(),
    onPrimary = TODO(),
    secondary = TODO(),
    onSecondary = TODO()
)

private val typography = AppTypography(
    // add the typography for example look at the first prob
    titleLarge = TextStyle(
        fontFamily = TODO(),
        fontWeight = TODO(),
        fontSize = TODO()
    ),
    titleNormal = TODO(),
    paragraph = TODO(),
    labelLarge = TODO(),
    labelNormal = TODO(),
    labelSmall = TODO()
)

private val shape = AppShape(
    container = TODO(),
    button = TODO()
)

private val size = AppSize(
    large = 24.dp,
    medium = 16.dp,
    normal = 12.dp,
    small = 8.dp
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    CompositionLocalProvider(
        localAppColorScheme provides colorScheme,
        localAppTypography provides typography,
        LocalAppSize provides size,
        localAppShape provides shape,
        content = content
    )
}

object AppTheme {
    val colorScheme : AppColorScheme
        @Composable get() = localAppColorScheme.current
    val typography : AppTypography
        @Composable get() = localAppTypography.current
    val shape : AppShape
        @Composable get() = localAppShape.current
    val size : AppSize
        @Composable get() = LocalAppSize.current
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = White,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = PrimaryLavender,

    secondary = AccentCyan,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceHigh,
    onSecondaryContainer = DarkTextPrimary,

    tertiary = PrimaryLavender,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurface,
    onTertiaryContainer = PrimaryLavender,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    surfaceVariant = DarkTextField,
    onSurfaceVariant = DarkTextSecondary,

    surfaceContainerLowest = DarkBottomBar,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceHigh,
    surfaceContainerHigh = DarkTextField,
    surfaceContainerHighest = DarkBorder,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorSoft,
    onErrorContainer = DarkBackground,

    outline = DarkBorder,
    outlineVariant = DarkIconInactive,

    inverseSurface = DarkTextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = PrimaryLavender,

    scrim = Black
)


private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = White,
    primaryContainer = PrimaryPurpleLight,
    onPrimaryContainer = White,

    secondary = AccentCyan,
    onSecondary = White,
    secondaryContainer = LightSurfacePurple,
    onSecondaryContainer = PrimaryPurple,

    tertiary = PrimaryLavender,
    onTertiary = LightTextPrimary,
    tertiaryContainer = LightSurfacePurple,
    onTertiaryContainer = PrimaryPurple,

    background = LightBackground,
    onBackground = LightTextPrimary,

    surface = LightSurface,
    onSurface = LightTextPrimary,

    surfaceVariant = LightTextField,
    onSurfaceVariant = LightTextSecondary,

    surfaceContainerLowest = LightBottomBar,
    surfaceContainerLow = LightSurface,
    surfaceContainer = LightSurfacePurple,
    surfaceContainerHigh = LightCard,
    surfaceContainerHighest = LightTextField,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorLightContainer,
    onErrorContainer = LightTextPrimary,

    outline = LightBorder,
    outlineVariant = LightIconInactive,

    inverseSurface = LightTextPrimary,
    inverseOnSurface = LightSurface,
    inversePrimary = PrimaryPurpleLight,

    scrim = Black
)

@Composable
fun RickAndMortyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}