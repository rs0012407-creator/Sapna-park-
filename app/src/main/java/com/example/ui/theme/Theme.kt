package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SapanaPrimaryDark,
    background = SapanaBackgroundDark,
    surface = SapanaSurfaceDark,
    surfaceVariant = SapanaSurfaceVariantDark,
    onBackground = SapanaOnSurfaceDark,
    onSurface = SapanaOnSurfaceDark,
    onSurfaceVariant = SapanaOnSurfaceVariantDark,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE)
)

private val LightColorScheme = lightColorScheme(
    primary = SapanaPrimary,
    onPrimary = SapanaOnPrimary,
    primaryContainer = SapanaPrimaryContainer,
    onPrimaryContainer = SapanaOnPrimaryContainer,
    secondary = SapanaSecondary,
    secondaryContainer = SapanaSecondaryContainer,
    onSecondaryContainer = SapanaOnSecondaryContainer,
    tertiary = SapanaTertiary,
    tertiaryContainer = SapanaTertiaryContainer,
    onTertiaryContainer = SapanaOnTertiaryContainer,
    background = SapanaBackground,
    surface = SapanaSurface,
    surfaceVariant = SapanaSurfaceVariant,
    onSurface = SapanaOnSurface,
    onSurfaceVariant = SapanaOnSurfaceVariant,
    outline = SapanaOutline,
    outlineVariant = SapanaOutlineVariant
)

@Composable
fun SapanaParkTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Use rich custom theme by default
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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SapanaParkTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
