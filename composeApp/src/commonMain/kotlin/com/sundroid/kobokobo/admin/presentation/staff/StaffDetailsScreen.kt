package com.sundroid.kobokobo.admin.presentation.staff

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.CustomText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailsScreen(viewModel: StaffDetailsViewModel, title: MutableState<String>) {
    val uiState by viewModel.uiState.collectAsState()
    var roleExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        title.value = "Staff Profile"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.errorMessage?.let { CustomText(it, color = MaterialTheme.colorScheme.error) }
            uiState.successMessage?.let { CustomText(it, color = MaterialTheme.colorScheme.primary) }

            uiState.staff?.let { staff ->
                // Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = staff.name, // Assuming name isn't available, using role or ID
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                         Text(
                            text = staff.roleName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StatusBadge(isActive = staff.active)
                    }
                }

                // Actions & Details Grid
                // Using Column for simplicity as it's likely a mobile view, but could be Row for desktop
                
                // Role Management Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                     elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                             Text(
                                text = "Role Management",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        
                        ExposedDropdownMenuBox(
                            expanded = roleExpanded,
                            onExpandedChange = { roleExpanded = !roleExpanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = staff.roleName,
                                onValueChange = {},
                                label = { Text("Assigned Role") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                uiState.availableRoles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role.name) },
                                        onClick = {
                                            viewModel.updateRole(role.id, role.name)
                                            roleExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                }

                // Account Status Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                     elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                     Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Account Actions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                         Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                             if (!staff.active) {
                                Button(
                                    onClick = { viewModel.setActive(true) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Activate Account")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.setActive(false) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Deactivate Account")
                                }
                            }
                        }
                     }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(isActive: Boolean) {
    val backgroundColor = if (isActive) 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.errorContainer
        
    val contentColor = if (isActive) 
        MaterialTheme.colorScheme.onPrimaryContainer 
    else 
        MaterialTheme.colorScheme.onErrorContainer
        
    val text = if (isActive) "Active" else "Inactive"
    val icon = if (isActive) Icons.Default.CheckCircle else Icons.Default.Block

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}
