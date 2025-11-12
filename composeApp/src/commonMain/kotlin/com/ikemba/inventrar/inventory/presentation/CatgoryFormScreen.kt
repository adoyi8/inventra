package com.ikemba.inventrar.inventory.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.core.presentation.components.CustomText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    viewModel: CategoryFormViewModel,
    onNavigateBack: () -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = uiState.categoryId != null
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. Handle Success Message (Snackbar)
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(true){
        title.value =  if (isEditMode) uiState.name else "Add Category"
    }

    // 2. Handle Deletion (Navigate Back)
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            // Navigate back to the Category List screen after deletion
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val showLoading = uiState.isInitialLoad || uiState.isSaving
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

            // Show content only when initial data is loaded or if in 'Add' mode
            if (!uiState.isInitialLoad) {
                // Input Fields
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { CustomText("Category Name *") },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { CustomText("Description (Optional)") },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !uiState.isSaving,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save/Create Button
                Button(
                    onClick = viewModel::saveCategory,
                    enabled = !uiState.isSaving && uiState.name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomText(if (isEditMode) "Save Changes" else "Create Category")
                }
            }
        }
    }
}