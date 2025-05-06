package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel

@Composable
fun ConfirmLogout(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visible = state.showConfirmLogout,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut(),
        content = {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Confirm Exit") },
                confirmButton = {
                    Button(onClick =
                    {
                         viewModel.logout()
                    }){
        CustomText("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        viewModel.hideShowConfirmLogout()
                    }){

                        Text("No")
                    }
                    //    showConfirmExitApplication = false
                },
                text = {
                    Column(modifier = Modifier.width(400.dp)) {

                            CustomText("Are you sure you want to logout?")

                    }
                }
            )
        }


    )
}