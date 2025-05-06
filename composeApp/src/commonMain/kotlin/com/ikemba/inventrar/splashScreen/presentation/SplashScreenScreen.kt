package com.ikemba.inventrar.splashScreen.presentation

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.CustomText
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SplashScreenRoot(
    viewModel: SplashScreenViewModel = koinViewModel<SplashScreenViewModel>(),

){

    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
           .fillMaxSize()
           .background(Color(0xFFF5F5F5)), // Light gray background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       SplashScreen()
    }
}



@Composable
fun SplashScreen() {


    var startAnimation by remember { mutableStateOf(false) }

    // Animate horizontal positions
    val logoOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -300f, // From left (-300) to center (0)
        animationSpec = tween(durationMillis = 1200, easing = EaseOutBack)
    )

    val textOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 300f, // From right (300) to center (0)
        animationSpec = tween(durationMillis = 1200, easing = EaseOutBack)
    )

    val fadeAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000)
    )

    LaunchedEffect(Unit) {
        delay(300) // Small delay for effect
        startAnimation = true
        delay(1500) // Wait for animation to finish

    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFF236ff9)),
        contentAlignment = Alignment.Center

    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.inventra_logo),
                contentDescription = "App Logo",
                colorFilter = ColorFilter.tint(color = Color.White),
                modifier = Modifier
                    .offset(x = logoOffset.dp)
                    .alpha(fadeAlpha)
                    .size(100.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomText(
                text = "Inventrar",
                size = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .offset(x = textOffset.dp)
                    .alpha(fadeAlpha)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreen()
}
