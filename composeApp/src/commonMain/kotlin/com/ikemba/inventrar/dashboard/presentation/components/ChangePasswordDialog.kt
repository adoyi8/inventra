package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.changePassword.presentation.ChangePasswordAction
import com.ikemba.inventrar.changePassword.presentation.ChangePasswordScreenRoot
import com.ikemba.inventrar.changePassword.presentation.ChangePasswordViewModel
import com.ikemba.inventrar.core.presentation.CustomGray
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordDialog(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visible = state.showChangePasswordDialog,
        enter = scaleIn(),
        exit = scaleOut(),
        content = {
            AlertDialog(
                modifier = Modifier.padding(vertical = 16.dp),
                onDismissRequest = {},
                containerColor = CustomGray,
                shape = RoundedCornerShape(1.dp),
                confirmButton = {
                },
                dismissButton = {
                    CustomButton(onClick = {
                        viewModel.hideChangePasswordDialog()
                    }, text = "Cancel")
                    //    showConfirmExitApplication = false
                },
                text = {
                    Column(modifier = Modifier.background(Color.Red)) {

                        val viewModel = koinViewModel<ChangePasswordViewModel>()
                        LaunchedEffect(true) {
                            val  action = ChangePasswordAction.OnToggleVisibility(true)
                            viewModel.onAction(action)
                        }
                        ChangePasswordScreenRoot(
                            viewModel = viewModel
                        )

                    }
                }
            )
        }


    )
}