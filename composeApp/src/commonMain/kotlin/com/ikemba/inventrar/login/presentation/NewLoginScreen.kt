package com.ikemba.inventrar.login.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// REMOVED: import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.BodyText
import com.ikemba.inventrar.core.presentation.components.CaptionText
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.core.presentation.components.HeadingText
import inventrar.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.dashboard.utils.Util.APP_NAME
import inventrar.composeapp.generated.resources.google_sign_in

// Your ViewModel and other imports...
// ...

// --- Define your new brand colors ---
val OgleBrandColor = Color(0xFFFFC244) // Glovo's signature yellow
val GoogleButtonBorderColor = Color(0xFFE0E0E0) // A light gray border
val GoogleButtonTextColor = Color(0xFF1F1F1F) // Dark text for readability
val MutedTextColor = Color(0xFF424242) // A softer gray for body text

/**
 * Root composable remains the same.
 */
@Composable
fun LoginScreenRoot(
    viewModel: UserViewModel, googleAuthProvider: GoogleAuthUiProvider
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onAction = viewModel::onAction,
        googleAuthProvider = googleAuthProvider
    )
}

/**
 * The new, multiplatform-safe LoginScreen.
 * It uses BoxWithConstraints to adapt the layout.
 */
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    googleAuthProvider: GoogleAuthUiProvider
) {
    // BoxWithConstraints is the KMP way to get screen dimensions
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(OgleBrandColor), // Full-screen brand color
        contentAlignment = Alignment.Center
    ) {
        // Determine if the screen is compact (like a phone) or expanded (like a desktop)
        val isCompact = maxWidth < 600.dp

        // Animate content visibility
        AnimatedVisibility(
            visible = state.isVisible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                animationSpec = tween(800),
                initialOffsetY = { it / 2 } // Slide in from top-middle
            ),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    // Use a responsive width for the content
                    .fillMaxWidth(if (isCompact) 0.9f else 0.5f) // 90% on mobile, 50% on desktop
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.5f)) // Pushes content down a bit

                // 1. App Name
                CustomText(
                    text = APP_NAME, // Your app name
                    size = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(Modifier.height(32.dp))

                // 2. Welcome Message
                HeadingText(
                    text = "Welcome!",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    size = 28.sp
                )
                Spacer(Modifier.height(12.dp))
                BodyText(
                    text = "Continue with Google to get started.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MutedTextColor,
                    size = 16.sp
                )

                // 3. Large spacer to push button towards the bottom
                Spacer(Modifier.weight(1f))

                // 4. Google Login Button
                GoogleLoginButton(
                    onClick = { onAction(LoginAction.OnGoogleLoginClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                // 5. Terms and Conditions
                Spacer(Modifier.height(24.dp))
                CaptionText(
                    text = "By continuing, you agree to Ogle's Terms of Service and Privacy Policy.",
                    textAlign = TextAlign.Center,
                    color = MutedTextColor,
                    size = 12.sp
                )
                Spacer(Modifier.weight(0.2f)) // A bit of padding at the bottom
            }
        }
    }

    // Loading Overlay - Unchanged (This is multiplatform safe)
    AnimatedVisibility(
        visible = state.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // Assuming ProgressDialog is a composable you have
        // ProgressDialog("Logging in, Please wait...")

        // Fallback placeholder
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0x88000000)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

    // Trigger animation - Unchanged (This is multiplatform safe)
    LaunchedEffect(Unit) {
        delay(300)
        onAction(LoginAction.SetVisible(true))
    }
}

/**
 * A specific button for Google Login.
 * This is already multiplatform-safe.
 */
@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = GoogleButtonTextColor
        ),
        border = BorderStroke(1.dp, GoogleButtonBorderColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // This is the correct KMP resource loading
            Image(
                painter = painterResource(Res.drawable.google_sign_in),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            CustomText(
                text = "Continue with Google",
                color = GoogleButtonTextColor,
                fontWeight = FontWeight.SemiBold,
                size = 16.sp
            )
        }
    }
}