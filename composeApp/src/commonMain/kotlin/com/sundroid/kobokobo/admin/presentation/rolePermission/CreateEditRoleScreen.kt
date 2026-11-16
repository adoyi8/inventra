package com.sundroid.kobokobo.admin.presentation.rolePermission


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.PermissionCategory
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.theme.TextSize
import com.sundroid.kobokobo.theme.TextSize.bodySmall


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditRoleScreen(
    viewModel: RolePermissionViewModel,
    userViewModel: com.sundroid.kobokobo.login.presentation.UserViewModel,
    onNavigateBack: () -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()
    val userState by userViewModel.state.collectAsState()

    val features = userState.currentUser?.businessFeatures ?: emptyList()
    val hasSalesFeature = features.isEmpty() || features.contains("Sales")
    val hasLaundryFeature = features.isEmpty() || features.contains("Laundry")
    val hasServicesFeature = features.isEmpty() || features.contains("Services")
    val hasHotelFeature = features.isEmpty() || features.contains("Hotels & Lodgings")

    val permissionCategories = mutableListOf(
        PermissionCategory.SYSTEM,
        PermissionCategory.BUSINESS,
        PermissionCategory.STAFF,
        PermissionCategory.REPORTS,
        PermissionCategory.CUSTOMER,
        PermissionCategory.EXPENSES
    )

    if (hasSalesFeature) {
        permissionCategories.add(PermissionCategory.SALES)
        permissionCategories.add(PermissionCategory.INVENTORY)
        permissionCategories.add(PermissionCategory.PRODUCT)
        permissionCategories.add(PermissionCategory.DAMAGES)
    }

    if (hasLaundryFeature) {
        permissionCategories.add(PermissionCategory.LAUNDRY)
    }

    if (hasServicesFeature) {
        permissionCategories.add(PermissionCategory.SERVICES)
    }

    if (hasHotelFeature) {
        permissionCategories.add(PermissionCategory.HOTELS)
    }

    LaunchedEffect(true){
      title.value =  if (uiState.isEditingRole) "Edit Role" else "Create Role"
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            userViewModel.refreshPermissions()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveRole() },
                        enabled = uiState.newRoleName.isNotBlank() && !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(4.dp))
                        CustomText("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Change the root Column to a LazyColumn for performance and proper scrolling
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp) // Optional: add padding at the bottom of the list
        ) {
            // Error messages
            item {
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        CustomText(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Role details form
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CustomText(
                            text = "Role Details",
                            style = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.newRoleName,
                            onValueChange = viewModel::updateRoleName,
                            label = { CustomText("Role Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.newRoleDescription,
                            onValueChange = viewModel::updateRoleDescription,
                            label = { CustomText("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 3
                        )
                    }
                }
            }

            // Permissions selection section
            item {
                // This Card now only contains the header and loading indicator
                Card(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CustomText(
                            text = "Permissions",
                            style = TextSize.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }


            // The permissions themselves are now items in the main LazyColumn
            if (!uiState.isLoading) {
                items(permissionCategories) { category ->
                    // We put the PermissionCategorySection inside a card-like container
                    // if we want to maintain the Card visual for each group.
                    // Or simply place it directly. Here, I'll put it in its own Card for grouping.
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        PermissionCategorySection(
                            category = category,
                            permissions = viewModel.getPermissionsByCategory(category),
                            selectedPermissionIds = uiState.selectedPermissionIds,
                            onPermissionToggle = viewModel::togglePermissionSelection,
                            onSelectAll = { viewModel.selectAllPermissionsInCategory(category) },
                            onDeselectAll = { viewModel.deselectAllPermissionsInCategory(category) },
                            areAllSelected = viewModel.areAllPermissionsInCategorySelected(category),
                            areSomeSelected = viewModel.areSomePermissionsInCategorySelected(category)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionCategorySection(
    category: PermissionCategory,
    permissions: List<Permission>,
    selectedPermissionIds: Set<String>,
    onPermissionToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    areAllSelected: Boolean,
    areSomeSelected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (areSomeSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    text = category.displayName,
                    style = TextSize.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    if (areSomeSelected && !areAllSelected) {
                        TextButton(onClick = onSelectAll) {
                            CustomText("Select All")
                        }
                    }
                    if (areSomeSelected) {
                        TextButton(onClick = onDeselectAll) {
                            CustomText("Deselect All")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            permissions.forEach { permission ->
                PermissionItem(
                    permission = permission,
                    isSelected = permission.id in selectedPermissionIds,
                    onToggle = { onPermissionToggle(permission.id) }
                )
            }
        }
    }
}

@Composable
fun PermissionItem(
    permission: Permission,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            CustomText(
                text = permission.name,
                style = TextSize.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            CustomText(
                text = permission.description,
                style = bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

