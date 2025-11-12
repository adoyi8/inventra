package com.ikemba.inventrar.login.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.rememberScrollState // <-- Import this
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.core.presentation.components.ProgressDialog
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo_and_text
import inventrar.composeapp.generated.resources.person
import inventrar.composeapp.generated.resources.pos_image
import inventrar.composeapp.generated.resources.visibility
import inventrar.composeapp.generated.resources.visibility_off
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch





@Composable
fun LoginVisuals(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFc9d8f7)), // Light blue background
        contentAlignment = Alignment.Center
    ) {
        // Decorative background circle
        Box(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .clip(CircleShape)
                .background(Color(0xFF1E73E8))
        )
        // Main Image
        Image(
            painter = painterResource(Res.drawable.pos_image),
            contentDescription = "POS System",
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

    // <-- Import this

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    googleAuthProvider: GoogleAuthUiProvider
) {
    val passwordVisible = remember { mutableStateOf(false) }
    // ✨ 1. Remember the scroll state
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            // ✨ 2. Apply the verticalScroll modifier
            modifier = Modifier
                .padding(32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.inventra_logo_and_text),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxWidth(0.4f)
            )
            Spacer(Modifier.height(40.dp))
            Text(
                "LOGIN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.userName,
                onValueChange = { onAction(LoginAction.OnUserNameChange(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.person),
                        contentDescription = "Username",
                        modifier = Modifier.size(24.dp)
                    )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible.value) Res.drawable.visibility_off else Res.drawable.visibility
                    val description = if (passwordVisible.value) "Hide Password" else "Show Password"
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = description,
                        modifier = Modifier
                            .size(24.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { passwordVisible.value = !passwordVisible.value }
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (state.errorMessage.isNotBlank()) {
                Text(
                    state.errorMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onAction(LoginAction.OnLoginButtonClicked) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E73E8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onAction(LoginAction.OnGoogleLoginClicked) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login with Google", color = Color.White)
            }
        }
    }
}