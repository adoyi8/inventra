package com.sundroid.kobokobo.damages.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.inventory.data.domain.model.Product


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDamageDialog(
    state: DamageState,
    onDismiss: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProductSelected: (Product?) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onReasonChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Damage") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Product Search Dropdown
                ProductSearchDropdown(
                    query = state.searchQuery,
                    onQueryChanged = onSearchQueryChanged,
                    filteredProducts = state.filteredProducts,
                    selectedProduct = state.selectedProduct,
                    onProductSelected = onProductSelected,
                    enabled = !state.isSubmitting
                )

                // 2. Quantity Input
                OutlinedTextField(
                    value = state.quantity,
                    onValueChange = onQuantityChanged,
                    label = { Text("Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSubmitting && state.selectedProduct != null
                )

                // 3. Reason Input
                OutlinedTextField(
                    value = state.reason,
                    onValueChange = onReasonChanged,
                    label = { Text("Reason (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSubmitting,
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                enabled = state.canSubmit
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Re-use the ProductSearchDropdown from the previous response,
// but update onProductSelected signature to accept nullable Product?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchDropdown(
    query: String,
    onQueryChanged: (String) -> Unit,
    filteredProducts: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product?) -> Unit, // Changed to nullable
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChanged(it)
                expanded = true
            },
            label = { Text("Search Product *") },
            trailingIcon = {
                if (selectedProduct != null) {
                    IconButton(onClick = { onProductSelected(null); onQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        if (filteredProducts.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 250.dp)
            ) {
                filteredProducts.forEach { product ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                Text("SKU: ${product.sku}", style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        onClick = {
                            onProductSelected(product)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}