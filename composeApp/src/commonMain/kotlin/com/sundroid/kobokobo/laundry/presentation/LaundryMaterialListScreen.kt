package com.sundroid.kobokobo.laundry.presentation

import androidx.compose.foundation.clickable
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
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryMaterial
import com.sundroid.kobokobo.dashboard.utils.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryMaterialListScreen(
    viewModel: LaundryMaterialViewModel,
    staffId: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var materialToEdit by remember { mutableStateOf<LaundryMaterial?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomText("Laundry Materials") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Material")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.materials.isEmpty()) {
                CustomText("No materials found. Add one!", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.materials) { material ->
                        MaterialItem(
                            material = material,
                            onEdit = { materialToEdit = material },
                            onDelete = { viewModel.deleteMaterial(material.id) }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showAddDialog || materialToEdit != null) {
        AddEditMaterialDialog(
            material = materialToEdit,
            onDismiss = {
                showAddDialog = false
                materialToEdit = null
            },
            onSave = { name, wash, iron, both ->
                if (materialToEdit != null) {
                    viewModel.updateMaterial(
                        materialToEdit!!.copy(
                            name = name,
                            washingPrice = wash,
                            ironingPrice = iron,
                            washAndIronPrice = both
                        ),
                        staffId
                    )
                } else {
                    viewModel.addMaterial(name, wash, iron, both, staffId)
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
fun MaterialItem(material: LaundryMaterial, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            CustomText(material.name, fontWeight = FontWeight.Bold)
            CustomText("Wash: ${Util.CURRENCY_CODE}${material.washingPrice}")
            CustomText("Iron: ${Util.CURRENCY_CODE}${material.ironingPrice}")
            CustomText("Both: ${Util.CURRENCY_CODE}${material.washAndIronPrice}")
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
fun AddEditMaterialDialog(
    material: LaundryMaterial?,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf(material?.name ?: "") }
    var washPrice by remember { mutableStateOf(material?.washingPrice?.toString() ?: "") }
    var ironPrice by remember { mutableStateOf(material?.ironingPrice?.toString() ?: "") }
    var bothPrice by remember { mutableStateOf(material?.washAndIronPrice?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { CustomText(if (material == null) "Add Material" else "Edit Material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { CustomText("Name (e.g. Shirt)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = washPrice,
                    onValueChange = { washPrice = it },
                    label = { CustomText("Washing Price") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ironPrice,
                    onValueChange = { ironPrice = it },
                    label = { CustomText("Ironing Price") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bothPrice,
                    onValueChange = { bothPrice = it },
                    label = { CustomText("Wash & Iron Price") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = washPrice.toDoubleOrNull() ?: 0.0
                    val i = ironPrice.toDoubleOrNull() ?: 0.0
                    val b = bothPrice.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(name, w, i, b)
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
