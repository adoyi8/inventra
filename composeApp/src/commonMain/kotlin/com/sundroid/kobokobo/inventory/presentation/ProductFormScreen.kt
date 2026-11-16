package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductFormViewModel,
    onNavigateBack: () -> Unit,
    title: MutableState<String>,
    onNavigateToAddCategory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = uiState.productId != null
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearSuccessMessage()
        }
    }
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onNavigateBack()
        }
    }
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    LaunchedEffect(true){
        title.value = if (isEditMode) "Edit ${uiState.name}" else "New Product"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            val isFabEnabled = !uiState.isLoading && !uiState.isDeleting && uiState.name.isNotBlank() && uiState.unitPrice.isNotBlank() && uiState.sku.isNotBlank()
            FloatingActionButton(
                onClick = { if (isFabEnabled) viewModel.saveProduct() },
                containerColor = if (isFabEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isFabEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Product")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.isLoading || uiState.isDeleting) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }

            // Error Dialog
            uiState.errorMessage?.let {
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    title = { Text("Error") },
                    text = { Text(it) },
                    confirmButton = {
                        TextButton(onClick = viewModel::clearError) { Text("OK") }
                    }
                )
            }
            
            // Add Stock Dialog
            if (uiState.isAddStockDialogOpen) {
                AddStockDialog(
                    stockToAdd = uiState.stockToAdd,
                    onStockToAddChange = viewModel::updateStockToAdd,
                    onDismiss = viewModel::closeAddStockDialog,
                    onConfirm = viewModel::addStock,
                    isLoading = uiState.isLoading
                )
            }

            if (!uiState.isInitialLoad) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Text(
                            text = if (isEditMode) "Update Product" else "Add New Product",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Divider()

                        // Form Fields
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            label = { Text("Product Name *") },
                            leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = uiState.unitPrice,
                                onValueChange = viewModel::updateUnitPrice,
                                label = { Text("Price *") },
                                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = uiState.costPrice,
                                onValueChange = viewModel::updateCostPrice,
                                label = { Text("Cost") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Stock Field
                        if (!isEditMode) {
                            OutlinedTextField(
                                value = uiState.stock,
                                onValueChange = viewModel::updateStock,
                                label = { Text("Initial Stock") },
                                leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = uiState.currentStock.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Current Stock") },
                                    leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Button(
                                    onClick = viewModel::openAddStockDialog,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add Stock")
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = uiState.sku,
                                onValueChange = viewModel::updateSku,
                                label = { Text("SKU *") },
                                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = uiState.sortOrder,
                                onValueChange = viewModel::updateSortOrder,
                                label = { Text("Sort Order") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        CategoryDropdown(
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategorySelected = viewModel::updateSelectedCategory,
                            onAddCategoryClick = onNavigateToAddCategory,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = viewModel::updateDescription,
                            label = { Text("Description") },
                            minLines = 3,
                            maxLines = 5,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                         Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = uiState.discount,
                                onValueChange = viewModel::updateDiscount,
                                label = { Text("Discount %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = uiState.vat,
                                onValueChange = viewModel::updateVat,
                                label = { Text("VAT %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryName = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }?.name ?: "Select Category"
    }

    // Safe dummy construction
    val dropdownItems = remember(categories) {
        listOf(
            Category(id = "", name = "None / Clear Selection", description = "", businessId = "", createdBy = "", updatedBy = "")
        ) + categories
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = selectedCategoryName,
            onValueChange = {},
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
            enabled = enabled,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropdownItems.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    },
                    contentPadding = PaddingValues(16.dp),
                    leadingIcon = {
                        if (category.id == selectedCategoryId) {
                            Icon(Icons.Default.Done, contentDescription = "Selected")
                        }
                    }
                )
            }
            Divider()
            DropdownMenuItem(
                text = { Text("Add New Category", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    onAddCategoryClick()
                    expanded = false
                },
                contentPadding = PaddingValues(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = "Add Category", tint = MaterialTheme.colorScheme.primary)
                }
            )
        }
    }
}

@Composable
fun AddStockDialog(
    stockToAdd: String,
    onStockToAddChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Stock") },
        text = {
            Column {
                Text("Enter the quantity to add to the current stock. This will be added to the existing count.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = stockToAdd,
                    onValueChange = onStockToAddChange,
                    label = { Text("Quantity to Add") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && stockToAdd.isNotBlank() && (stockToAdd.toDoubleOrNull() ?: 0.0) > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Add")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancel")
            }
        }
    )
}
