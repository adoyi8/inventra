package com.ikemba.inventrar.inventory.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.inventory.data.domain.model.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductFormViewModel,
    onNavigateBack: () -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = uiState.productId != null
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. Handle Success and Deletion
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearSuccessMessage()
        }
    }
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onNavigateBack() // Navigate away after successful deletion
        }
    }
    LaunchedEffect(true){
        title.value = if (isEditMode)  uiState.name else "Add Product"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                        },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = viewModel::deleteProduct,
                            enabled = !uiState.isLoading && !uiState.isDeleting
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val showLoading = uiState.isInitialLoad || uiState.isLoading || uiState.isDeleting
            if (showLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Error Dialog
            uiState.errorMessage?.let {
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    title = { CustomText("Error") },
                    text = { CustomText(it) },
                    confirmButton = {
                        TextButton(onClick = viewModel::clearError) { CustomText("OK") }
                    }
                )
            }

            if (!uiState.isInitialLoad) {
                // Scrollable Content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // Product Name
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = viewModel::updateName,
                            label = { CustomText("Product Name *") },
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        // Unit Price and SKU Row (Example of two inputs in a row)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.unitPrice,
                                onValueChange = viewModel::updateUnitPrice,
                                label = { CustomText("Unit Price *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = uiState.sku,
                                onValueChange = viewModel::updateSku,
                                label = { CustomText("SKU *") },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        // Category Dropdown (You'll need a custom Composable for this)
                        CategoryDropdown(
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategorySelected = viewModel::updateSelectedCategory,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        // Description
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = viewModel::updateDescription,
                            label = { CustomText("Description") },
                            minLines = 3,
                            maxLines = 5,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        // Discount and VAT Row
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = uiState.discount,
                                onValueChange = viewModel::updateDiscount,
                                label = { CustomText("Discount (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = uiState.vat,
                                onValueChange = viewModel::updateVat,
                                label = { CustomText("VAT (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save/Create Button
                Button(
                    onClick = viewModel::saveProduct,
                    enabled = !uiState.isLoading && !uiState.isDeleting && uiState.name.isNotBlank() && uiState.unitPrice.isNotBlank() && uiState.sku.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomText(if (isEditMode) "Save Changes" else "Create Product")
                }
            }
        }
    }
}



// Assume 'Category' is defined as:
// data class Category(val id: String, val name: String, ...)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    // State to manage whether the dropdown menu is expanded
    var expanded by remember { mutableStateOf(false) }

    // State to display the currently selected category name, or a placeholder
    val selectedCategoryName = remember(selectedCategoryId, categories) {
        categories.find { it.id == selectedCategoryId }?.name ?: "Select Category"
    }

    // Include an "None" or "No Category" option at the start of the list
    val dropdownItems = remember(categories) {
        listOf(Category(id = "", name = "None / Clear Selection", description = "", businessId = "", createdBy = "", updatedBy = "")) + categories
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && enabled },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            // This is the anchor component (the text field)
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = selectedCategoryName,
            onValueChange = { /* readOnly, so nothing happens here */ },
            label = { CustomText("Category") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
            },
            enabled = enabled
        )

        // The actual dropdown menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropdownItems.forEach { category ->
                DropdownMenuItem(
                    text = { CustomText(category.name) },
                    onClick = {
                        // 1. Fix the onselect: Call the ViewModel function
                        onCategorySelected(category.id)
                        // 2. Close the menu
                        expanded = false
                    },
                    contentPadding = PaddingValues(16.dp),
                    // Highlight the currently selected item
                    leadingIcon = {
                        if (category.id == selectedCategoryId) {
                            Icon(Icons.Default.Done, contentDescription = "Selected")
                        }
                    }
                )
            }
        }
    }
}