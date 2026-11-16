package com.sundroid.kobokobo.admin.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.repository.RolePermissionRepository
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.Staff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffDetailsUiState(
    val isLoading: Boolean = false,
    val staff: Staff? = null,
    val availableRoles: List<Role> = emptyList(), // NEW: Hold available roles
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class StaffDetailsViewModel(
    private val businessRepository: BusinessRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val staffId: String,
    private val businessId: String
) : ViewModel()
{
    private val _uiState = MutableStateFlow(StaffDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
        loadRoles()

    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            businessRepository.getStaffById(staffId)
                .onSuccess { s -> _uiState.update { it.copy(isLoading = false, staff = s) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load staff") } }
        }
    }
    private fun loadRoles() {
        viewModelScope.launch {
            // You might want a separate loading state for roles, or share the main one.
            // For simplicity, we won't set isLoading=true here to avoid flickering if staff loads fast.
            rolePermissionRepository.getAllRoles(businessId)
                .collect { roles ->
                    _uiState.update { it.copy(availableRoles = roles) }
                }
        }
    }
    fun setActive(active: Boolean) {
        val current = uiState.value.staff ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            businessRepository.setStaffActive(current.staffId, active)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, staff = current.copy(active = active), successMessage = if (active) "Staff activated" else "Staff deactivated") }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to update status") } }
        }
    }


    // Updated to use the repository's updateStaffRole
    fun updateRole(roleId: String, roleName: String) {
        val current = uiState.value.staff ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            // Assuming businessRepository.updateStaffRole updates the staff document in Firestore
            businessRepository.updateStaffRole(current.staffId, newRole = roleId, newRoleName = roleName)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Update the local staff object to reflect the change instantly
                            staff = current.copy(roleId = roleId, roleName = roleName),
                            successMessage = "Role updated"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to update role") }
                }
        }
    }
}














