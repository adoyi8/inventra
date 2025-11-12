package com.ikemba.inventrar.splashScreen.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.collectAsState
import com.ikemba.inventrar.admin.presentation.AdminViewModel
import com.ikemba.inventrar.app.AuthNavGraph
import com.ikemba.inventrar.app.BusinessNavigationGraph
import com.ikemba.inventrar.app.GlobalNavGraph
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.business.presentation.BusinessViewModel
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.utils.Util.APP_NAME
import com.ikemba.inventrar.dropdowndata.presentation.DropDownSettingsViewModel
import com.ikemba.inventrar.login.presentation.OgleBrandColor
import com.ikemba.inventrar.login.presentation.UserViewModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SplashScreenRoot(
    dashboardViewModel: MutableState<DashboardViewModel?>? = null, userViewModel: UserViewModel, businessViewModel: BusinessViewModel, dropDownSettingsViewModel: DropDownSettingsViewModel = koinViewModel(), adminViewModel: AdminViewModel = koinViewModel(), googleAuthUiProvider: GoogleAuthUiProvider, businessListViewModel: BusinessListViewModel = koinViewModel()

)
{
    val state by userViewModel.state.collectAsStateWithLifecycle()
    






    Box(Modifier.fillMaxSize()) {
        when {
            // Case 1: User is not logged in.
            state.currentUser == null -> {
                AuthNavGraph(
                    dashboardViewModel = dashboardViewModel,
                    userViewModel = userViewModel,
                    businessViewModel = businessViewModel,
                    dropDownSettingsViewModel = dropDownSettingsViewModel,
                    adminViewModel = adminViewModel,
                    googleAuthUiProvider = googleAuthUiProvider,
                    businessListViewModel = businessListViewModel,

                    )
            }

            // Case 2: User is logged in, but has NOT selected a business.
            state.currentUser?.businessId == null -> {
                GlobalNavGraph(
                    dashboardViewModel = dashboardViewModel,
                    userViewModel = userViewModel,
                    businessViewModel = businessViewModel,
                    dropDownSettingsViewModel = dropDownSettingsViewModel,
                    adminViewModel = adminViewModel,
                    googleAuthUiProvider = googleAuthUiProvider,
                    businessListViewModel = businessListViewModel,

                    )
            }

            // Case 3: User is logged in AND has selected a business.
            else -> {
                BusinessNavigationGraph(
                    dashboardViewModel = dashboardViewModel,
                    userViewModel = userViewModel,
                    businessViewModel = businessViewModel,
                    dropDownSettingsViewModel = dropDownSettingsViewModel,
                    adminViewModel = adminViewModel,
                    googleAuthUiProvider = googleAuthUiProvider,
                    businessListViewModel = businessListViewModel,

                    )
            }
        }
            SplashScreen(onFinished = {})
    }
}



@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(true) }

    // This effect runs once when the composable enters the screen
    LaunchedEffect(Unit) {
        delay(6000) // Total splash screen duration
        visible = false
        delay(1000) // Duration of exit animation
        onFinished()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(1000)) +
                slideOut(
                    targetOffset = { IntOffset(it.width / 2, it.height / 2) },
                    animationSpec = tween(1000)
                ) +
                shrinkOut(
                    shrinkTowards = Alignment.BottomEnd,
                    animationSpec = tween(1000)
                )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OgleBrandColor), // A nice dark slate color
            contentAlignment = Alignment.Center
        ) {
            AnimatedAppContent()
        }
    }
}


// --- 3. The Animated Content (Logo and Text) ---
@Composable
private fun AnimatedAppContent() {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500) // Start animation after a short delay
        contentVisible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated Image
        AnimatedVisibility(
            visible = contentVisible,
            enter = scaleIn(
                animationSpec = tween(2000),
                initialScale = 0.5f
            ) + fadeIn(animationSpec = tween(2000))
        ) {
            Image(
                painter = painterResource(Res.drawable.inventra_logo), // IMPORTANT: Provide your own logo here
                contentDescription = "App Logo",
                modifier = Modifier.size(128.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Text
        AnimatedVisibility(
            visible = contentVisible,
            enter = slideInVertically(
                animationSpec = tween(2000, delayMillis = 1000),
                initialOffsetY = { it }
            ) + fadeIn(
                animationSpec = tween(2000, delayMillis = 1000)
            )
        ) {
            Text(
                text = APP_NAME,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

