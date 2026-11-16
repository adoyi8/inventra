package com.sundroid.kobokobo.admin.presentation.rolePermission

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sundroid.kobokobo.admin.data.domain.model.Role
import kotlinx.coroutines.delay

@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementScreen(
    viewModel: RolePermissionViewModel,
    onNavigateToCreateRole: () -> Unit,
    onNavigateToEditRole: (Role) -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        title.value = "Role Management"
        viewModel.initializeSystemPermissions()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateRole,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Role")
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val width = maxWidth
            val columns = when {
                width < 600.dp -> 1
                width < 1000.dp -> 2
                else -> 3
            }

            // Error/Success Messages
            Column(modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)) {
                uiState.error?.let { error ->
                    Snackbar(containerColor = MaterialTheme.colorScheme.error) { Text(error) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                uiState.successMessage?.let { message ->
                    Snackbar(containerColor = MaterialTheme.colorScheme.primary) { Text(message) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 72.dp)
                ) {
                    itemsIndexed(uiState.roles) { index, role ->
                        RoleCard(
                            role = role,
                            index = index,
                            onEdit = { onNavigateToEditRole(role) },
                            onDelete = { viewModel.deleteRole(role) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    role: Role,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 4 } + scaleIn(initialScale = 0.9f)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (role.isSystemRole) MaterialTheme.colorScheme.tertiaryContainer 
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (role.isSystemRole) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Actions
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        if (!role.isSystemRole) {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = role.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (role.isSystemRole) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("System", style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            border = null,
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    minLines = 2
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${role.permissions.size} Permissions Granted",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
