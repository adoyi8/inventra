package com.sundroid.kobokobo.department

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.core.presentation.components.CustomText

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

    LaunchedEffect(departmentToEdit) {
        name = departmentToEdit?.name ?: ""
        description = departmentToEdit?.description ?: ""
        title.value = if (isEditing) "Edit ${departmentToEdit?.name}" else "Create New Department"
    }

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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveDepartment(name, description) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save Department")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Business else Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isEditing) "Edit Department" else "New Department",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fill in the details below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Department Name") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Description (Optional)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}