package com.ikemba.inventrar.department

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.core.presentation.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditDepartmentScreen(
    viewModel: DepartmentViewModel,
    onNavigateBack: () -> Unit,
    title: MutableState<String>
) {
    val departmentToEdit by viewModel.selectedDepartment.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Populate fields when editing
    LaunchedEffect(departmentToEdit) {
        name = departmentToEdit?.name ?: ""
        description = departmentToEdit?.description ?: ""
        title.value = if (isEditing) departmentToEdit?.name?: "Edit Department" else "Create Department"

    }

    // Listen for UI events (save success, error)
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DepartmentViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is DepartmentViewModel.UiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { CustomText(if (isEditing) "Edit Department" else "Create Department") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveDepartment(name, description)
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save Department")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { CustomText("Department Name*") },
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { CustomText("Description") }
            )
        }
    }
}