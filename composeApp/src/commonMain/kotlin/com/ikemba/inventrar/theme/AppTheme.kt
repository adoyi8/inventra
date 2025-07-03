package com.ikemba.inventrar.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// --- Common Colors (if any, often standard black/white) ---
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// --- Theme 1: Sophisticated & Inviting Colors ---
val Burgundy = Color(0xFF800020)
val Gold = Color(0xFFD4AF37)
val DeepTeal = Color(0xFF4A6B7C)
val Cream = Color(0xFFFBF8F0)
val RichCream = Color(0xFFF7F4EB)
val EspressoBrown = Color(0xFF2E1D1B)
val CharcoalDark = Color(0xFF1C1C1C)
val SoftDarkGrey = Color(0xFF2C2C2C)
val MaterialRedError = Color(0xFFB00020) // Light theme error
val MaterialRedErrorDark = Color(0xFFCF6679) // Dark theme error

// --- Theme 2: Modern & Chic Colors ---
val MutedTeal = Color(0xFF006A7C)
val VibrantCoral = Color(0xFFFF6F61)
val CoolGreyLight = Color(0xFFC8D2D8)
val DarkGreyMain = Color(0xFF333333)
val NearBlack = Color(0xFF121212)
val VeryDarkGrey = Color(0xFF1E1E1E)
val LightGreyText = Color(0xFFE0E0E0)
val SophisticatedInvitingLightColors = lightColorScheme(
    primary = Burgundy,
    onPrimary = White,
    primaryContainer = Gold, // Often used for container background, like a filled button
    onPrimaryContainer = EspressoBrown, // Text/icon on primaryContainer
    secondary = Gold,
    onSecondary = EspressoBrown,
    secondaryContainer = DeepTeal, // Another container color
    onSecondaryContainer = White, // Text/icon on secondaryContainer
    tertiary = DeepTeal, // An accent color, often used for smaller elements
    onTertiary = White,
    background = Cream,
    onBackground = EspressoBrown,
    surface = RichCream, // Cards, sheets, etc.
    onSurface = EspressoBrown,
    surfaceVariant = Cream, // Background for components like tabs, chips
    onSurfaceVariant = EspressoBrown,
    error = MaterialRedError,
    onError = White,
    errorContainer = MaterialRedError, // Error container background
    onErrorContainer = White, // Text/icon on error container
    outline = EspressoBrown.copy(alpha = 0.5f) // For borders
)

val SophisticatedInvitingDarkColors = darkColorScheme(
    primary = Burgundy, // Keep primary consistent or slightly darker
    onPrimary = White,
    primaryContainer = Gold.copy(alpha = 0.8f), // Slightly muted gold for dark mode container
    onPrimaryContainer = Black, // Text/icon on primaryContainer
    secondary = Gold,
    onSecondary = Black,
    secondaryContainer = DeepTeal.copy(alpha = 0.8f),
    onSecondaryContainer = White,
    tertiary = DeepTeal,
    onTertiary = White,
    background = CharcoalDark,
    onBackground = White, // White text on dark background
    surface = SoftDarkGrey,
    onSurface = White,
    surfaceVariant = CharcoalDark,
    onSurfaceVariant = White,
    error = MaterialRedErrorDark,
    onError = Black,
    errorContainer = MaterialRedErrorDark,
    onErrorContainer = Black,
    outline = White.copy(alpha = 0.3f) // Lighter outline for dark mode
)

@Composable
fun AppTheme(    darkTheme: Boolean = isSystemInDarkTheme(),content: @Composable () -> Unit,){
    val colorScheme = if (darkTheme) {
        SophisticatedInvitingDarkColors // Or ModernChicDarkColors
    } else {
        SophisticatedInvitingLightColors // Or ModernChicLightColors
    }
    MaterialTheme(colorScheme = colorScheme, content= content)
}