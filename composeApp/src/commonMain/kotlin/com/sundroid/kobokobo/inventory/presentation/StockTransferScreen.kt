package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.*
import com.sundroid.kobokobo.inventory.data.domain.model.StockMovement
import com.sundroid.kobokobo.inventory.data.domain.model.MovementType
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockTransferScreen(
    viewModel: StockTransferViewModel = koinViewModel(),
    onTransfer: (StockMovement) -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeadingText("Stock Transfer")

        uiState.errorMessage?.let { 
            CustomText(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        uiState.successMessage?.let { 
            CustomText(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        OutlinedTextField(
            value = uiState.selectedProductId,
            onValueChange = { viewModel.updateSelectedProduct(it) },
            label = { Text("Product ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.selectedFromLocationId,
            onValueChange = { viewModel.updateSelectedFromLocation(it) },
            label = { Text("From Location ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.selectedToLocationId,
            onValueChange = { viewModel.updateSelectedToLocation(it) },
            label = { Text("To Location ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.quantity,
            onValueChange = { viewModel.updateQuantity(it) },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.reason,
            onValueChange = { viewModel.updateReason(it) },
            label = { Text("Reason") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { viewModel.transferStock() },
                enabled = uiState.selectedProductId.isNotBlank() && uiState.selectedToLocationId.isNotBlank() && uiState.quantity.isNotBlank() && !uiState.isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Transfer Stock")
                }
            }
        }
    }
}
