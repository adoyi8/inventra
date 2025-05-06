package com.ikemba.inventrar.changePassword.presentation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.CustomGray
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.core.presentation.components.ProgressDialog
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo_and_text
import inventrar.composeapp.generated.resources.visibility
import inventrar.composeapp.generated.resources.visibility_off
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordScreenRoot(
    viewModel: ChangePasswordViewModel = koinViewModel<ChangePasswordViewModel>(),
){

    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomGray), // Light gray background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChangePasswordScreen(
            state = state,
            onAction = { action ->
                viewModel.onAction(action)
            }
        )
    }
}
@Composable
fun ChangePasswordScreen(
    state: ChangePasswordState,
    onAction: (ChangePasswordAction)-> Unit
) {



    // Trigger animation after composition
    LaunchedEffect(Unit) {
        delay(500)
        state.isVisible = true
    }
    val passwordVisible = remember{ mutableStateOf(false) }
    val oldPasswordVisible = remember{ mutableStateOf(false) }
    val newPasswordVisible = remember{ mutableStateOf(false) }
    val confirmPasswordVisible = remember{ mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {



            // Right ChangePassword Form Section
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(CustomGray),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = state.isVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).background(color = Color.White).fillMaxWidth(0.95f).fillMaxHeight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(modifier = Modifier.padding(32.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.Center){
                                Image(
                                    painter = painterResource(Res.drawable.inventra_logo_and_text),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxWidth(0.3f)
                                )
                            }
                            Spacer(Modifier.weight(0.5f))
                            CustomText(
                                "Change Password",
                                size = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            OutlinedTextField(
                                value = state.oldPassword,
                                onValueChange = {

                                    onAction(ChangePasswordAction.OnOldPasswordChanged(it))

                                },
                                visualTransformation = if (oldPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    if(oldPasswordVisible.value) {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility_off),
                                            contentDescription = "Hide Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(
                                                PointerIcon.Hand).clickable(
                                                onClick = {
                                                    oldPasswordVisible.value = false
                                                }
                                            )
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility),
                                            contentDescription = "Show Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    oldPasswordVisible.value = true
                                                }
                                            )
                                        )
                                    }
                                },
                                label = { Text("Old Password") },
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.25f))
                            OutlinedTextField(
                                value = state.newPassword,
                                onValueChange = {

                                    onAction(ChangePasswordAction.OnNewPasswordChanged(it))

                                },
                                visualTransformation = if (newPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    if(newPasswordVisible.value) {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility_off),
                                            contentDescription = "Hide Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    newPasswordVisible.value = false
                                                }
                                            )
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility),
                                            contentDescription = "Show Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    newPasswordVisible.value = true
                                                }
                                            )
                                        )
                                    }
                                },
                                label = { Text("New Password") },
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.25f))
                            OutlinedTextField(
                                value = state.confirmPassword,
                                onValueChange = {
                                    onAction(ChangePasswordAction.OnConfirmPasswordChanged(it))
                                },
                                visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    if(confirmPasswordVisible.value) {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility_off),
                                            contentDescription = "Hide Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    confirmPasswordVisible.value = false
                                                }
                                            )
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(Res.drawable.visibility),
                                            contentDescription = "Show Password",
                                            modifier = Modifier.size(24.dp).pointerHoverIcon(PointerIcon.Hand).clickable(
                                                onClick = {
                                                    confirmPasswordVisible.value = true
                                                }
                                            )
                                        )
                                    }
                                },
                                label = { Text("Confirm Password") },
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.25f))
                            CustomText(
                                state.errorMessage,
                                size = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            CustomButton(
                                onClick = {
                                    onAction(ChangePasswordAction.OnChangePasswordButtonClicked)
                                },
                                text = "Submit",
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    }
                }

            }

        AnimatedVisibility(
            visible = state.isLoading,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            ProgressDialog("Processing, Just a sec...")
        }
    }
}