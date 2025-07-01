package com.ikemba.inventrar.cart.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.cart.presentation.ReceiptModel
import com.ikemba.inventrar.cart.presentation.ReceiptScreen
import com.ikemba.inventrar.core.presentation.SecondaryColor
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfirmTransactionDialog(viewModel: DashboardViewModel, text: String) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = state.value.showConfirmTransaction) {
        AlertDialog(
            containerColor = SecondaryColor,
            onDismissRequest = {},
            title = { CustomText("Confirm") },
            confirmButton = {
                CustomButton(onClick = {
                    viewModel.toggleShowConfirmTransaction(false)
                    viewModel.postSale()
                }, text = "Yes")
            },
            dismissButton = {
                CustomButton(onClick = {viewModel.toggleShowConfirmTransaction(false)}, text = "No")
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(400.dp)
                ) {
                    // CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    // Spacer(modifier = Modifier.width(8.dp))
                    CustomText(text)
                }
            }

        )
    }
}
@Composable
fun PaymentMethodNotAvailableDialog(viewModel: DashboardViewModel, text: String) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = state.value.showPaymentMethodNotAvailableDialog) {
        AlertDialog(

            containerColor = SecondaryColor,
            onDismissRequest = {
            },
            title = { CustomText("Info") },
            confirmButton = {
                CustomButton(onClick = {
                    viewModel.toggleShowPaymentMethodNotAvailable(false)
                }, text = "Okay")
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(400.dp)
                ) {
                    CustomText("This Payment Method is currently not available")
                }
            }

        )
    }
}
@Composable
fun PostingSalesDialog(viewModel: DashboardViewModel, text: String) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = state.value.showPostSalesDialog) {
        AlertDialog(
            containerColor = SecondaryColor,
            onDismissRequest = {},
            title = { CustomText("Posting Sales ...") },
            confirmButton = {
            },
            dismissButton = {
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(400.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomText(text)
                }
            }

        )
    }
}

@Composable
fun ReceiptDialog(visibleState: MutableTransitionState<Boolean>, onDismiss: () -> Unit, receiptModel: ReceiptModel) {
    //val state = viewModel.state.collectAsStateWithLifecycle()
    AnimatedVisibility(visibleState = visibleState) {
        AlertDialog(
            shape = RectangleShape,
            containerColor = Color.White,
            onDismissRequest = {
                onDismiss
            },
            title = {},
            confirmButton = {
                onDismiss
            },
            dismissButton = {
                CustomButton(onClick = onDismiss,
                    "Cancel")
            },
            text = {
               ReceiptScreen(receiptModel)
            }

        )
    }
}