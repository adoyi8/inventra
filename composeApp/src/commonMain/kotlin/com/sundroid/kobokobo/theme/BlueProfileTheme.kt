package com.sundroid.kobokobo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// A tasteful bluish palette for the user profile area
private val BluePrimary = Color(0xFF276EF1) // Vibrant but elegant blue
private val BluePrimaryDark = Color(0xFF1F57C3)
private val BlueContainer = Color(0xFFDCE6FF)
private val BlueContainerDark = Color(0xFF0E1A33)
private val BlueTertiary = Color(0xFF5E9BFF)
private val SurfaceLight = Color(0xFFF7F9FF)
private val SurfaceDark = Color(0xFF0F141C)
private val OnDark = Color(0xFFE6F0FF)

private val BlueLightColors = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueContainer,
    onPrimaryContainer = Color(0xFF0A1E4A),
    secondary = BluePrimary,
    onSecondary = Color.White,
    secondaryContainer = BlueContainer,
    onSecondaryContainer = Color(0xFF0A1E4A),
    tertiary = BlueTertiary,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = Color(0xFF0A1E4A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0A1E4A),
    surfaceVariant = BlueContainer,
    onSurfaceVariant = Color(0xFF17305E),
)

private val BlueDarkColors = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = OnDark,
    secondary = BluePrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = BlueContainerDark,
    onSecondaryContainer = OnDark,
    tertiary = BlueTertiary,
    onTertiary = Color.Black,
    background = SurfaceDark,
    onBackground = OnDark,
    surface = SurfaceDark,
    onSurface = OnDark,
    surfaceVariant = BlueContainerDark,
    onSurfaceVariant = OnDark,
)

@Composable
fun BlueProfileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) BlueDarkColors else BlueLightColors
    MaterialTheme(colorScheme = colors, content = content)
}




