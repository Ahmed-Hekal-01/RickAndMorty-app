package com.example.rickandmortyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
