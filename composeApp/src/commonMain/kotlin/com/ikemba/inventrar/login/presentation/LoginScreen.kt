package com.ikemba.inventrar.login.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenRoot(
    viewModel: LoginViewModel = koinViewModel<LoginViewModel>(),
){

    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
           .fillMaxSize()
           .background(Color(0xFFF5F5F5)), // Light gray background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(true){
          delay(5000)

        }
        LoginScreen(
            state = state,
            onAction = { action ->
                viewModel.onAction(action)
            }
        )
    }
}
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction)-> Unit
) {



    // Trigger animation after composition
    LaunchedEffect(Unit) {
        delay(500)
        state.isVisible = true
    }
    val passwordVisible = remember{mutableStateOf(false)}

    Box(Modifier.fillMaxSize()) {


        Row(modifier = Modifier.fillMaxSize()) {
            // Left Image Section
            Box(
                modifier = Modifier
                    .weight(1f).fillMaxHeight()
                    .background(Color(0xFFc9d8f7)), // Light blue background
                contentAlignment = Alignment.Center
            ) {
                this@Row.AnimatedVisibility(
                    visible = state.isVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.7f)
                            .clip(CircleShape)
                            .background(Color(0xFF1E73E8))
                    )
                }
                this@Row.AnimatedVisibility(
                    visible = state.isVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Image(
                        painter = painterResource(Res.drawable.pos_image),
                        contentDescription = "POS System",
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }

            // Right Login Form Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFf5f5f5)),
                contentAlignment = Alignment.Center
            ) {
                this@Row.AnimatedVisibility(
                    visible = state.isVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).background(color = Color.White).fillMaxWidth(0.95f).fillMaxHeight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                                Image(
                                    painter = painterResource(Res.drawable.inventra_logo_and_text),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxWidth(0.3f)
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                            CustomText(
                                "LOGIN",
                                size = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = state.userName,
                                onValueChange = {
                                    onAction(LoginAction.OnUserNameChange(it))
                                },
                                label = { Text("Username") },
                                modifier = Modifier.fillMaxWidth(),

                                trailingIcon = {
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
                                onValueChange = {

                                    onAction(LoginAction.OnPasswordChange(it))

                                },
                                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    if(passwordVisible.value) {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility_off),
                                            contentDescription = "Hide Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    passwordVisible.value = false
                                                }
                                            )
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility),
                                            contentDescription = "Show Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    passwordVisible.value = true
                                                }
                                            )
                                        )
                                    }
                                },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                state.errorMessage,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    onAction(LoginAction.OnLoginButtonClicked)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF1E73E8
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Login", color = Color.White)
                            }
                        }
                    }
                }

            }
        }
        AnimatedVisibility(
            visible = state.isLoading,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            ProgressDialog("Login in, Please wait...")
        }
    }
}
