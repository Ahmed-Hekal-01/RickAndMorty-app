package com.example.rickandmortyapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkColorScheme = AppColorScheme(

    // =========================
    // Backgrounds
    // =========================

    background = DarkBackground,
    onBackground = DarkTextSecondary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    cardBackground = DarkSurfaceHigh,
    darkCardBackground = DarkBottomBar,

    surfaceVariant = DarkTextField,
    onSurfaceVariant = DarkTextMuted,


    // =========================
    // Primary Brand Colors
    // =========================

    primary = PrimaryPurple,
    onPrimary = White,

    primaryDark = PrimaryPurpleDark,
    primaryLight = PrimaryLavender,


    // =========================
    // Accent / Interactive
    // =========================

    secondary = AccentCyan,
    onSecondary = DarkBackground,

    accent = AccentCyan,
    neonAccent = AccentCyan,


    // =========================
    // Text Colors
    // =========================

    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,


    // =========================
    // Icons
    // =========================

    iconPrimary = White,
    iconSecondary = DarkTextSecondary,
    inactiveIcon = DarkIconInactive,


    // =========================
    // States
    // =========================

    error = ErrorRed,
    onError = White,

    success = SuccessGreen,
    warning = PrimaryLavender,


    // =========================
    // Borders & Dividers
    // =========================

    border = DarkBorder,
    divider = DarkBorder,


    // =========================
    // Glow / Effects
    // =========================

    glowPrimary = PrimaryPurple,
    glowSecondary = AccentCyan,


    // =========================
    // Gradients
    // =========================

    gradientStart = PrimaryPurple,
    gradientEnd = PrimaryLavender,
    glowTop = PrimaryPurple,
    glowBottom = AccentCyan,

    screenBackground = DarkBackground,
    surfaceContainer = DarkSurface,
    inputField = DarkTextField,

    googleRed = Color(0xFFEA4335),
)

private val lightColorScheme = AppColorScheme(

    // =========================
    // Backgrounds
    // =========================

    background = LightBackground,
    onBackground = LightTextSecondary,

    surface = LightSurface,
    onSurface = LightTextPrimary,

    cardBackground = LightCard,
    darkCardBackground = LightBottomBar,

    surfaceVariant = LightTextField,
    onSurfaceVariant = LightTextMuted,


    // =========================
    // Primary Brand Colors
    // =========================

    primary = PrimaryPurple,
    onPrimary = White,

    primaryDark = PrimaryPurpleDark,
    primaryLight = PrimaryLavender,


    // =========================
    // Accent / Interactive
    // =========================

    secondary = AccentCyan,
    onSecondary = White,

    accent = AccentCyan,
    neonAccent = AccentCyan,


    // =========================
    // Text Colors
    // =========================

    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,


    // =========================
    // Icons
    // =========================

    iconPrimary = LightTextPrimary,
    iconSecondary = LightTextSecondary,
    inactiveIcon = LightIconInactive,


    // =========================
    // States
    // =========================

    error = ErrorRed,
    onError = White,

    success = SuccessGreen,
    warning = PrimaryLavender,


    // =========================
    // Borders & Dividers
    // =========================

    border = LightBorder,
    divider = LightBorder,


    // =========================
    // Glow / Effects
    // =========================

    glowPrimary = PrimaryPurple,
    glowSecondary = AccentCyan,


    // =========================
    // Gradients
    // =========================

    gradientStart = PrimaryPurple,
    gradientEnd = PrimaryLavender,
    glowTop = LightSurfacePurple,
    glowBottom = AccentCyan,

    screenBackground = LightBackground,
    surfaceContainer = LightSurface,
    inputField = LightTextField,

    googleRed = Color(0xFFEA4335)
)
private val typography = AppTypography(
    titleLarge = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    ),

    titleNormal = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    ),

    paragraph = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),

    labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),

    labelNormal = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),

    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 1.sp
    )
)
private val shape = AppShape(
    container = RoundedCornerShape(24.dp),
    button = RoundedCornerShape(14.dp),
)
private val size = AppSize(
    large = 24.dp,
    medium = 16.dp,
    normal = 12.dp,
    small = 8.dp,

    screenPadding = 32.dp,
    cardPadding = 20.dp,
    formPadding = 26.dp,
    fieldHeight = 56.dp,
    buttonHeight = 58.dp,
    glowWidth = 260.dp,
    glowHeight = 420.dp,
    glowBlur = 80.dp,
    otpBoxWidth = 43.dp,
    otpBoxHeight = 64.dp,

    bottomBarHeight = 72.dp,
    navIconSize = 48.dp,
    searchHeight = 56.dp,
    homeCardWidth = 155.dp,
    homeCardHeight = 230.dp,
    characterImageSize = 110.dp
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    CompositionLocalProvider(
        LocalAppColorScheme provides colorScheme,
        localAppTypography provides typography,
        LocalAppSize provides size,
        localAppShape provides shape,
        content = content
    )
}

object AppTheme {
    val colorScheme : AppColorScheme
        @Composable get() = LocalAppColorScheme.current
    val typography : AppTypography
        @Composable get() = localAppTypography.current
    val shape : AppShape
        @Composable get() = localAppShape.current
    val size : AppSize
        @Composable get() = LocalAppSize.current
}

