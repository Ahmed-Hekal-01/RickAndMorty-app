package com.example.rickandmortyapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp


@Stable
data class AppColorScheme(

    // Backgrounds

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,

    val cardBackground: Color,
    val darkCardBackground: Color,

    val surfaceVariant: Color,
    val onSurfaceVariant: Color,



    // Primary Brand Colors

    val primary: Color,
    val onPrimary: Color,

    val primaryDark: Color,
    val primaryLight: Color,



    // Accent / Interactive

    val secondary: Color,
    val onSecondary: Color,

    val accent: Color,
    val neonAccent: Color,



    // Text Colors

    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,



    // Icons

    val iconPrimary: Color,
    val iconSecondary: Color,
    val inactiveIcon: Color,



    // States

    val error: Color,
    val onError: Color,

    val success: Color,
    val warning: Color,



    // Borders & Dividers

    val border: Color,
    val divider: Color,



    // Glow / Effects

    val glowPrimary: Color,
    val glowSecondary: Color,



    // Gradients

    val gradientStart: Color,
    val gradientEnd: Color,

    val glowTop: Color,
    val glowBottom: Color,
    val screenBackground: Color,
    val surfaceContainer: Color,
    val inputField: Color,
    val googleRed: Color
)


@Stable
data class AppTypography(
    val titleLarge: TextStyle,
    val titleNormal: TextStyle,
    val paragraph: TextStyle,
    val labelLarge: TextStyle,
    val labelNormal: TextStyle,
    val labelSmall: TextStyle
)

@Stable
data class AppShape(
    val container: Shape,
    val button: Shape
)

@Stable
data class AppSize(
    val large: Dp,
    val medium: Dp,
    val normal: Dp,
    val small: Dp,
    val screenPadding: Dp,
    val cardPadding: Dp,
    val formPadding: Dp,
    val fieldHeight: Dp,
    val buttonHeight: Dp,
    val glowWidth: Dp,
    val glowHeight: Dp,
    val glowBlur: Dp,
    val otpBoxWidth: Dp,
    val otpBoxHeight: Dp,
    val bottomBarHeight: Dp,
    val navIconSize: Dp,
    val searchHeight: Dp,
    val homeCardWidth: Dp,
    val homeCardHeight: Dp,
    val characterImageSize: Dp
)

val LocalAppColorScheme = staticCompositionLocalOf {

    AppColorScheme(

        background = Color.Unspecified,
        onBackground = Color.Unspecified,

        surface = Color.Unspecified,
        onSurface = Color.Unspecified,

        cardBackground = Color.Unspecified,
        darkCardBackground = Color.Unspecified,

        surfaceVariant = Color.Unspecified,
        onSurfaceVariant = Color.Unspecified,

        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,

        primaryDark = Color.Unspecified,
        primaryLight = Color.Unspecified,

        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified,

        accent = Color.Unspecified,
        neonAccent = Color.Unspecified,

        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textMuted = Color.Unspecified,

        iconPrimary = Color.Unspecified,
        iconSecondary = Color.Unspecified,
        inactiveIcon = Color.Unspecified,


        error = Color.Unspecified,
        onError = Color.Unspecified,

        success = Color.Unspecified,
        warning = Color.Unspecified,

        border = Color.Unspecified,
        divider = Color.Unspecified,

        glowPrimary = Color.Unspecified,
        glowSecondary = Color.Unspecified,

        gradientStart = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        glowTop = Color.Unspecified,
        glowBottom = Color.Unspecified,
        screenBackground = Color.Unspecified,
        surfaceContainer = Color.Unspecified,
        inputField = Color.Unspecified,
        googleRed = Color.Unspecified
    )
}

val localAppTypography = staticCompositionLocalOf {
    AppTypography(
        titleLarge = TextStyle.Default,
        titleNormal = TextStyle.Default,
        paragraph = TextStyle.Default,
        labelLarge = TextStyle.Default,
        labelNormal = TextStyle.Default,
        labelSmall = TextStyle.Default
    )
}

val localAppShape = staticCompositionLocalOf {
    AppShape(
        container = RectangleShape,
        button = RectangleShape
    )
}
// staticCompositionLocalOf is implicitly passing the data down the Ui Tree
val LocalAppSize = staticCompositionLocalOf {
    AppSize(
        large = Dp.Unspecified,
        medium = Dp.Unspecified,
        normal = Dp.Unspecified,
        small = Dp.Unspecified,
        screenPadding = Dp.Unspecified,
        cardPadding = Dp.Unspecified,
        formPadding = Dp.Unspecified,
        fieldHeight= Dp.Unspecified,
        buttonHeight= Dp.Unspecified,
        glowWidth= Dp.Unspecified,
        glowHeight= Dp.Unspecified,
        glowBlur= Dp.Unspecified ,
        otpBoxWidth =Dp.Unspecified ,
        otpBoxHeight = Dp.Unspecified ,
        bottomBarHeight = Dp.Unspecified,
        navIconSize = Dp.Unspecified,
        searchHeight = Dp.Unspecified,
        homeCardWidth = Dp.Unspecified,
        homeCardHeight = Dp.Unspecified,
        characterImageSize = Dp.Unspecified
    )
}