package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.CoreErrorScreen
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel



@Composable
fun SavingSalesDialog(viewModel: DashboardViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = state.value.showSavingSalesDialog) {
        AlertDialog(
            onDismissRequest = {viewModel.toggleShowSavingSalesDialog(false)},
            title = { Text("Info!") },
            confirmButton = {
                CustomButton(
                    {viewModel.toggleShowSavingSalesDialog(false)},
                    text = "Ok" )
            },

            text = {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CustomText("You currently do not have internet to complete this transaction. Your transaction will be saved and automatically processed as soon as internet is available")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Checkbox(
                            checked = viewModel.state.value.disableShowSavingSalesDialog,
                            onCheckedChange = {
                                viewModel.toggleDisableShowSavingSalesDialog(it)
                            })
                        CustomText("Do not show this again")
                    }
                }
            }

        )
    }
}