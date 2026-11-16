package com.sundroid.kobokobo.services.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.services.data.domain.model.ServicesMaterial
import com.sundroid.kobokobo.dashboard.utils.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesMaterialListScreen(
    viewModel: ServicesMaterialViewModel,
    staffId: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var materialToEdit by remember { mutableStateOf<ServicesMaterial?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomText("Services Prices") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Price")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.materials.isEmpty()) {
                CustomText("No services prices found. Add one!", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.materials) { material ->
                        ServicesPriceItem(
                            material = material,
                            onEdit = { materialToEdit = material },
                            onDelete = { viewModel.deleteMaterial(material.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showAddDialog || materialToEdit != null) {
        ServicesAddEditPriceDialog(
            material = materialToEdit,
            onDismiss = {
                showAddDialog = false
                materialToEdit = null
            },
            onSave = { name, price ->
                if (materialToEdit != null) {
                    viewModel.updateMaterial(
                        materialToEdit!!.copy(
                            name = name,
                            price = price
                        ),
                        staffId
                    )
                } else {
                    viewModel.addMaterial(name, price, staffId)
                }
                showAddDialog = false
                materialToEdit = null
            }
        )
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or toast
            viewModel.clearError()
        }
    }
}

@Composable
fun ServicesPriceItem(material: ServicesMaterial, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            CustomText(material.name, fontWeight = FontWeight.Bold)
            CustomText("${Util.CURRENCY_CODE}${material.price}")
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ServicesAddEditPriceDialog(
    material: ServicesMaterial?,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf(material?.name ?: "") }
    var price by remember { mutableStateOf(material?.price?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { CustomText(if (material == null) "Add Services Price" else "Edit Services Price") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { CustomText("Service Name (e.g. Adult Cut)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { CustomText("Price") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(name, p)
                    }
                }
            ) {
                CustomText("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                CustomText("Cancel")
            }
        }
    )
}
