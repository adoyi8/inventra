package com.sundroid.kobokobo.admin.presentation.rolePermission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.PermissionCategory
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.useCase.*
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RolePermissionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val roles: List<Role> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val selectedRole: Role? = null,
    val selectedPermissions: List<Permission> = emptyList(),
    val availablePermissions: List<Permission> = emptyList(),
    val isCreatingRole: Boolean = false,
    val isEditingRole: Boolean = false,
    val newRoleName: String = "",
    val newRoleDescription: String = "",
    val selectedPermissionIds: Set<String> = emptySet()
)

class RolePermissionViewModel(
    private val getAllRolesUseCase: GetAllRolesUseCase,
    private val getAllPermissionsUseCase: GetAllPermissionsUseCase,
    private val createRoleUseCase: CreateRoleUseCase,
    private val updateRoleUseCase: UpdateRoleUseCase,
    private val deleteRoleUseCase: DeleteRoleUseCase,
    private val getRolePermissionsUseCase: GetRolePermissionsUseCase,
    private val assignPermissionsToRoleUseCase: AssignPermissionsToRoleUseCase,
    private val removePermissionsFromRoleUseCase: RemovePermissionsFromRoleUseCase,
    private val initializeBusinessRolesUseCase: InitializeBusinessRolesUseCase,
    private val initializeSystemPermissionsUseCase: InitializeSystemPermissionsUseCase
) : ViewModel()
{

    private val _uiState = MutableStateFlow(RolePermissionUiState())
    val uiState = _uiState.asStateFlow()

    private val _businessId = MutableStateFlow<String?>(null)
    val businessId = _businessId.asStateFlow()

    fun setBusinessId(businessId: String?) {
        businessId?.let {
            _businessId.value = businessId
            loadRoles(it)
            loadPermissions()
        }
    }

    private fun loadRoles(businessId: String) {
        viewModelScope.launch {
            getAllRolesUseCase(businessId)
                .onEach { roles ->
                    _uiState.update { it.copy(roles = roles) }
                }
                .catch { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
                .collect()
        }
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            getAllPermissionsUseCase()
                .onEach { permissions ->
                    _uiState.update { 
                        it.copy(
                            permissions = permissions,
                            availablePermissions = permissions
                        ) 
                    }
                }
                .catch { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
                .collect()
        }
    }

    fun startCreatingRole() {
        _uiState.update { 
            it.copy(
                isCreatingRole = true,
                isEditingRole = false,
                newRoleName = "",
                newRoleDescription = "",
                selectedPermissionIds = emptySet()
            ) 
        }
    }

    fun startEditingRole(role: Role) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isEditingRole = true,
                    isCreatingRole = false,
                    selectedRole = role,
                    newRoleName = role.name,
                    newRoleDescription = role.description,
                    selectedPermissionIds = role.permissions.toSet()
                ) 
            }
            
            // Load role permissions
            val businessId = _businessId.value ?: return@launch
            getRolePermissionsUseCase(role.id, businessId)
                .onSuccess { permissions ->
                    _uiState.update { it.copy(selectedPermissions = permissions) }
                }
                .onError { error ->
                    _uiState.update { it.copy(error = error.toString()) }
                }
        }
    }

    fun cancelRoleOperation() {
        _uiState.update { 
            it.copy(
                isCreatingRole = false,
                isEditingRole = false,
                selectedRole = null,
                newRoleName = "",
                newRoleDescription = "",
                selectedPermissionIds = emptySet(),
                selectedPermissions = emptyList()
            ) 
        }
    }

    fun updateRoleName(name: String) {
        _uiState.update { it.copy(newRoleName = name) }
    }

    fun updateRoleDescription(description: String) {
        _uiState.update { it.copy(newRoleDescription = description) }
    }

    fun togglePermissionSelection(permissionId: String) {
        _uiState.update { currentState ->
            val newSelection = if (permissionId in currentState.selectedPermissionIds) {
                currentState.selectedPermissionIds - permissionId
            } else {
                currentState.selectedPermissionIds + permissionId
            }
            currentState.copy(selectedPermissionIds = newSelection)
        }
    }

    fun selectAllPermissionsInCategory(category: PermissionCategory) {
        _uiState.update { currentState ->
            val categoryPermissions = currentState.permissions
                .filter { it.category == category }
                .map { it.id }
            
            val newSelection = currentState.selectedPermissionIds + categoryPermissions
            currentState.copy(selectedPermissionIds = newSelection)
        }
    }

    fun deselectAllPermissionsInCategory(category: PermissionCategory) {
        _uiState.update { currentState ->
            val categoryPermissions = currentState.permissions
                .filter { it.category == category }
                .map { it.id }
            
            val newSelection = currentState.selectedPermissionIds - categoryPermissions.toSet()
            currentState.copy(selectedPermissionIds = newSelection)
        }
    }

    fun saveRole() {
        val businessId = _businessId.value ?: return
        val currentState = _uiState.value
        
        if (currentState.newRoleName.isBlank()) {
            _uiState.update { it.copy(error = "Role name cannot be empty") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val role = if (currentState.isEditingRole && currentState.selectedRole != null) {
                // Update existing role
                currentState.selectedRole.copy(
                    name = currentState.newRoleName,
                    description = currentState.newRoleDescription,
                    permissions = currentState.selectedPermissionIds.toList()
                )
            } else {
                // Create new role
                Role(
                    name = currentState.newRoleName,
                    description = currentState.newRoleDescription,
                    businessId = businessId,
                    permissions = currentState.selectedPermissionIds.toList()
                )
            }

            val result = if (currentState.isEditingRole) {
                updateRoleUseCase(role)
            } else {
                createRoleUseCase(role)
            }

            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = if (currentState.isEditingRole) "Role updated successfully" else "Role created successfully",
                        isCreatingRole = false,
                        isEditingRole = false,
                        selectedRole = null,
                        newRoleName = "",
                        newRoleDescription = "",
                        selectedPermissionIds = emptySet()
                    ) 
                }
                loadRoles(businessId)
            }.onError { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = error.toString()
                    ) 
                }
            }
        }
    }

    fun deleteRole(role: Role) {
        val businessId = _businessId.value ?: return
        
        if (role.isSystemRole) {
            _uiState.update { it.copy(error = "Cannot delete system roles") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            deleteRoleUseCase(role.id, businessId)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Role deleted successfully"
                        ) 
                    }
                    loadRoles(businessId)
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.toString()
                        ) 
                    }
                }
        }
    }

    fun initializeBusinessRoles(createdBy: String) {
        val businessId = _businessId.value ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            initializeBusinessRolesUseCase(businessId, createdBy)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Business roles initialized successfully"
                        ) 
                    }
                    loadRoles(businessId)
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.toString()
                        ) 
                    }
                }
        }
    }

    fun initializeSystemPermissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            initializeSystemPermissionsUseCase()
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "System permissions initialized successfully"
                        ) 
                    }
                    loadPermissions()
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.toString()
                        ) 
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun getPermissionsByCategory(category: PermissionCategory): List<Permission> {
        return _uiState.value.permissions.filter { it.category == category }
    }

    fun isPermissionSelected(permissionId: String): Boolean {
        return permissionId in _uiState.value.selectedPermissionIds
    }

    fun areAllPermissionsInCategorySelected(category: PermissionCategory): Boolean {
        val categoryPermissions = getPermissionsByCategory(category)
        return categoryPermissions.isNotEmpty() && categoryPermissions.all { 
            it.id in _uiState.value.selectedPermissionIds 
        }
    }

    fun areSomePermissionsInCategorySelected(category: PermissionCategory): Boolean {
        val categoryPermissions = getPermissionsByCategory(category)
        return categoryPermissions.any { it.id in _uiState.value.selectedPermissionIds }
    }
}

