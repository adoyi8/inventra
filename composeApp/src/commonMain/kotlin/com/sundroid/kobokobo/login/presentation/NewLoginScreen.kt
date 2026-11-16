package com.sundroid.kobokobo.login.presentation

// REMOVED: import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.core.presentation.components.BodyText
import com.sundroid.kobokobo.core.presentation.components.CaptionText
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.HeadingText
import com.sundroid.kobokobo.dashboard.utils.Util.APP_NAME
import com.sundroid.kobokobo.theme.AppTheme
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.google_sign_in
import kotlinx.coroutines.delay
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch

// Your ViewModel and other imports...
// ...

// --- Define your new brand colors ---
val KoboKoboBrandColor = Color(0xFFFFC244) // Glovo's signature yellow
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
@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    googleAuthProvider: GoogleAuthUiProvider
) {
    // BoxWithConstraints is the KMP way to get screen dimensions

    AppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainer), // Full-screen brand color
            contentAlignment = Alignment.Center
        )
        {
            // Determine if the screen is compact (like a phone) or expanded (like a desktop)
            val isCompact = maxWidth < 600.dp

            // Share Button at top right
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                com.sundroid.kobokobo.utils.ShareAppButton(
                    textToShare = "Download Inventra App from here! https://github.com/adoyi8/inventra/releases/download/v1.0/composeApp-release.apk"
                )
            }

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
                        style = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(Modifier.height(32.dp))

                    // 2. Welcome Message
                    HeadingText(
                        text = "Welcome!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        size = 28.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    BodyText(
                        text = "Continue with Google to get started.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        size = 16.sp
                    )

                    // 3. Large spacer to push button towards the bottom
                    Spacer(Modifier.weight(1f))

                    var isTermsAccepted by remember { mutableStateOf(false) }

                    // 4. Terms and Conditions
                    val uriHandler = LocalUriHandler.current
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val annotatedString = buildAnnotatedString {
                        append("I agree to Skobo's ")
                        pushStringAnnotation(tag = "TOS", annotation = "https://kobokobo-8840e.web.app/")
                        withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.SemiBold)) {
                            append("Terms of Service")
                        }
                        pop()
                        append(" and ")
                        pushStringAnnotation(tag = "PRIVACY", annotation = "https://kobokobo-8840e.web.app/")
                        withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.SemiBold)) {
                            append("Privacy Policy")
                        }
                        pop()
                        append(".")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = isTermsAccepted,
                            onCheckedChange = { isTermsAccepted = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        ClickableText(
                            text = annotatedString,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                textAlign = TextAlign.Start,
                                color = onSurfaceColor,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(tag = "TOS", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        uriHandler.openUri(it.item)
                                    }
                                annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        uriHandler.openUri(it.item)
                                    }
                            }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // 5. Google Login Button
                    GoogleLoginButton(
                        onClick = { 
                            if (isTermsAccepted) {
                                onAction(LoginAction.OnGoogleLoginClicked)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please accept the Terms of Service and Privacy Policy to continue.")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = true
                    )

                    Spacer(Modifier.weight(0.2f)) // A bit of padding at the bottom
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Trigger animation - Unchanged (This is multiplatform safe)
        LaunchedEffect(Unit) {
            delay(300)
            onAction(LoginAction.SetVisible(true))
        }
    }
}

/**
 * A specific button for Google Login.
 * This is already multiplatform-safe.
 */
@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = GoogleButtonTextColor,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray
        ),
        border = BorderStroke(1.dp, if (enabled) GoogleButtonBorderColor else Color.Transparent)
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
                style = 16.sp
            )
        }
    }
}