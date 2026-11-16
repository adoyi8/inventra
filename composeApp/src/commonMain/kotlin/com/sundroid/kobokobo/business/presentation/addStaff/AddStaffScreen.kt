package com.sundroid.kobokobo.business.presentation.addStaff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.business.presentation.AddStaffViewModel
import com.sundroid.kobokobo.core.presentation.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffScreen(viewModel: AddStaffViewModel, title: MutableState<String>) {
    val uiState by viewModel.uiState.collectAsState()
    var roleExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        title.value = "Add New Staff"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
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
                    .fillMaxSize()
                    .widthIn(max = 600.dp) // Limit width on large screens
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // Header Section
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    CustomText(
                        text = "Onboard New Team Member",
                        style = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CustomText(
                        text = "Enter email to verify and assign role details.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
                    OutlinedTextField(
                        value = uiState.emailQuery,
                        onValueChange = { viewModel.onEmailQueryChanged(it) },
                        label = { Text("Staff's Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.verifyEmail() },
                        enabled = uiState.emailQuery.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Verify Email")
                        }
                    }

                    // Messages
                    uiState.error?.let {
                        CustomText(it, color = MaterialTheme.colorScheme.error)
                    }
                    uiState.infoMessage?.let {
                        CustomText(it, color = MaterialTheme.colorScheme.primary)
                    }

                    AnimatedVisibility(visible = uiState.foundUser != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Found User Info
                            uiState.foundUser?.let { user ->
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp))
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = user.fullName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = user.email,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Role Selection
                            ExposedDropdownMenuBox(
                                expanded = roleExpanded,
                                onExpandedChange = { roleExpanded = !roleExpanded }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = uiState.selectedRole,
                                    onValueChange = {},
                                    label = { Text("Assign Role") },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = roleExpanded,
                                    onDismissRequest = { roleExpanded = false }
                                ) {
                                    uiState.roles.forEach { role ->
                                        DropdownMenuItem(
                                            text = { Text(role.name) },
                                            onClick = {
                                                viewModel.onRoleSelected(role)
                                                roleExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Department Selection
                            ExposedDropdownMenuBox(
                                expanded = deptExpanded,
                                onExpandedChange = { deptExpanded = !deptExpanded }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = uiState.departments.firstOrNull { it.first == uiState.selectedDepartmentId }?.second ?: "",
                                    onValueChange = {},
                                    label = { Text("Assign Department") },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                    leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = deptExpanded,
                                    onDismissRequest = { deptExpanded = false }
                                ) {
                                    uiState.departments.forEach { (id, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                viewModel.onDepartmentSelected(id)
                                                deptExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Branch Selection
                            ExposedDropdownMenuBox(
                                expanded = branchExpanded,
                                onExpandedChange = { branchExpanded = !branchExpanded }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = uiState.branches.firstOrNull { it.first == uiState.selectedBranchId }?.second ?: "",
                                    onValueChange = {},
                                    label = { Text("Assign Branch") },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
                                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = branchExpanded,
                                    onDismissRequest = { branchExpanded = false }
                                ) {
                                    uiState.branches.forEach { (id, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                viewModel.onBranchSelected(id)
                                                branchExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.addStaff(uiState.selectedRole) },
                                enabled = uiState.selectedRole.isNotBlank()
                                        && uiState.selectedDepartmentId.isNotBlank()
                                        && !uiState.isLoading,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Complete Onboarding", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}