package com.ikemba.inventrar.heldOrder.presentation.component

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
import com.ikemba.inventrar.heldOrder.presentation.HeldOrderViewModel
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfirmVoid(viewModel: HeldOrderViewModel = koinViewModel(), paginationRequest: PaginationRequest) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = state.showConfirmVoidOrder,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut(),
        content = {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Confirm") },
                confirmButton = {
                    Button(onClick =
                    {
                         viewModel.voidOrder(paginationRequest =paginationRequest )
                    }){
        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        viewModel.hideAllDialogs()
                    }){

                        Text("No")
                    }
                    //    showConfirmExitApplication = false
                },
                text = {
                    Column(modifier = Modifier.width(400.dp)) {

                            Text("Voiding this will remove it from your paused orders, Continue?")

                    }
                }
            )
        }


    )
}