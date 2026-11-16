package com.sundroid.kobokobo.business.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.core.presentation.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(viewModel: PaymentMethodsViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddMethodClick() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment Method")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.methods.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CustomText("No payment methods found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.methods) { method ->
                        PaymentMethodItem(
                            method = method,
                            onEdit = { viewModel.onEditMethodClick(method) },
                            onDelete = { viewModel.onDeleteMethod(method) }
                        )
                    }
                }
            }

            if (state.showDialog) {
                PaymentMethodDialog(
                    state = state,
                    onDismiss = { viewModel.onDismissDialog() },
                    onSave = { viewModel.onSaveMethod() },
                    onNameChange = { viewModel.onNameChange(it) },
                    onActiveChange = { viewModel.onActiveChange(it) }
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CustomText(text = method.name, fontWeight = FontWeight.Bold, style = 18.sp)
                CustomText(
                    text = if (method.active) "Active" else "Inactive",
                    color = if (method.active) Color.Green else Color.Red,
                    style = 14.sp
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun PaymentMethodDialog(
    state: PaymentMethodsState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onNameChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (state.editingMethod == null) "Add Payment Method" else "Edit Payment Method") },
        text = {
            Column {
                OutlinedTextField(
                    value = state.methodName,
                    onValueChange = onNameChange,
                    label = { Text("Method Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Active")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = state.methodActive,
                        onCheckedChange = onActiveChange
                    )
                }
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.error, color = Color.Red)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
