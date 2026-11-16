package com.sundroid.kobokobo.admin.presentation.branch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchManagementScreen() {
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Hook create/update actions */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Branch")
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Branch Management",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create or update branch details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider()

                    // Form Fields
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text("Branch Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )
                    
                    // Placeholder for future fields like Address, Phone, etc.
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Additional Details",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Future updates will include address coordinates and contact details here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
