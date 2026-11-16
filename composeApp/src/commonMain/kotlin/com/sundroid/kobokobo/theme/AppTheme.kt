package com.sundroid.kobokobo.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


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



// --- New Refined Color Palette ---
val MulberryRose = Color(0xFFD67B91) // The "Visible Burgundy"
val ChampagneGold = Color(0xFFE9C46A) // Softer, more modern gold
val MutedSageTeal = Color(0xFF8AB6BF) // Lighter teal for better dark-mode contrast
val DeepEspresso = Color(0xFF1A1616) // Warmer dark background
val SurfaceGrey = Color(0xFF252323)   // Soft elevated surface


// --- Hospitality Optimized Palette ---
val LuminousBlackCherry = Color(0xFFE57373) // High-visibility "Burgundy"
val HoneyGold = Color(0xFFFFB300)           // Punchy gold for "Place Order" buttons
val SlateTeal = Color(0xFF80CBC4)            // Clear contrast for "Paid/Complete"
val NightCafe = Color(0xFF121010)            // Deep, near-black for battery saving
val RaisedSurface = Color(0xFF1E1B1B)        // For Table Cards / Menu Items

val RestaurantDarkColors = darkColorScheme(
    primary = LuminousBlackCherry,
    onPrimary = Color(0xFF4B0009), // High contrast dark red text
    primaryContainer = Color(0xFF8C1D2F), // For "Active" categories
    onPrimaryContainer = Color(0xFFFFDADA),

    secondary = HoneyGold, // Perfect for "Add to Cart" or "Total Amount"
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF694400),
    onSecondaryContainer = Color(0xFFFFDF9E),

    tertiary = SlateTeal, // Use for "Status: Served" or "Table Available"
    onTertiary = Color(0xFF003734),
    tertiaryContainer = Color(0xFF00504C),
    onTertiaryContainer = Color(0xFF9CF2EB),

    background = NightCafe,
    onBackground = Color(0xFFEDE0DE),

    surface = RaisedSurface,
    onSurface = Color(0xFFEDE0DE),
    surfaceVariant = Color(0xFF322C2C), // Elevated card background
    onSurfaceVariant = Color(0xFFD8C2C0), // Subtle secondary text

    outline = Color(0xFF9F8C8A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)
val SophisticatedInvitingDarkColors = darkColorScheme(
    primary = MulberryRose,
    onPrimary = Color(0xFF3E101C), // Deep dark red for contrast on the rose
    primaryContainer = Color(0xFF723343), // Deep burgundy for containers
    onPrimaryContainer = Color(0xFFFFD9E1), // Light pink text on deep red

    secondary = ChampagneGold,
    onSecondary = Color(0xFF3C2E00),
    secondaryContainer = Color(0xFF574500),
    onSecondaryContainer = Color(0xFFFFE08D),

    tertiary = MutedSageTeal,
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF334B4F),
    onTertiaryContainer = Color(0xFFBFE8ED),

    background = DeepEspresso,
    onBackground = Color(0xFFECE0DF), // Slightly off-white to reduce eye strain

    surface = SurfaceGrey,
    onSurface = Color(0xFFECE0DF),
    surfaceVariant = Color(0xFF3D3334),
    onSurfaceVariant = Color(0xFFD1C2C3),

    outline = Color(0xFF9A8D8E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)
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



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
//fun AppTheme(    darkTheme: Boolean = isSystemInDarkTheme(),content: @Composable () -> Unit,){
    fun AppTheme(    darkTheme: Boolean = true,content: @Composable () -> Unit,){

        val colorScheme = if (darkTheme) {
            RestaurantDarkColors // Or ModernChicDarkColors
    } else {
        SophisticatedInvitingLightColors // Or ModernChicLightColors
    }
    MaterialExpressiveTheme(colorScheme = colorScheme, content= content)
}

object TextSize {
    val titleMedium = 16.sp
    val titleSmall = 14.sp
    val bodyMedium = 14.sp
    val bodySmall = 12.sp
    val titleLarge = 22.sp
}