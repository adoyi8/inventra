package com.sundroid.kobokobo.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.useCase.GetAllRolesUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetDepartmentsUseCase
import com.sundroid.kobokobo.business.data.domain.model.AppUser
import com.sundroid.kobokobo.business.data.domain.model.StaffRole
import com.sundroid.kobokobo.business.data.domain.useCase.AddStaffUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.FindUserByEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddStaffUiState(
    val isLoading: Boolean = false,
    val emailQuery: String = "",
    val foundUser: AppUser? = null,
    val infoMessage: String? = null,
    val error: String? = null,
    val roles: List<Role> = emptyList(),
    val selectedRole: String = "",
    val selectedDepartmentId: String = "",
    val departments: List<Pair<String, String>> = emptyList(), // id to name
    val selectedBranchId: String = "",
    val branches: List<Pair<String, String>> = emptyList() // id to name
)

class AddStaffViewModel(
    private val findUserByEmailUseCase: FindUserByEmailUseCase,
    private val addStaffUseCase: AddStaffUseCase,
    private val currentBusinessId: String,
    private val getDepartmentsUseCase: GetDepartmentsUseCase, // <-- INJECT
    private val getAllRolesUseCase: GetAllRolesUseCase,
) : ViewModel()
{
    private val _uiState = MutableStateFlow(AddStaffUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDropdownData() // <-- CALL on init
    }
    private fun loadDropdownData() {
        viewModelScope.launch {
            // Combine the flows to get updates from both
            combine(
                getDepartmentsUseCase(currentBusinessId),
                getAllRolesUseCase(currentBusinessId)
                // You would add getBranchesUseCase(currentBusinessId) here if you had it
            ) { departments, roles ->

                _uiState.update {
                    it.copy(
                        departments = departments.map { dept -> dept.id to dept.name },
                        roles = roles// Map to just names, as per your original UI logic
                        // branches = branches.map { branch -> branch.id to branch.name }
                    )
                }
            }.catch { e -> // Handle any errors during fetching
                _uiState.update {
                    it.copy(error = "Failed to load departments or roles.")
                }
            }.collect() // Start collecting the combined flow
        }
    }


    fun onEmailQueryChanged(email: String) {
        _uiState.update {
            it.copy(emailQuery = email, foundUser = null, infoMessage = null, error = null)
        }
    }

    fun onRoleSelected(role: Role) {
        _uiState.update { it.copy(selectedRole = role.id) }
    }

    fun onDepartmentSelected(departmentId: String) {
        _uiState.update { it.copy(selectedDepartmentId = departmentId) }
    }

    fun onBranchSelected(branchId: String) {
        _uiState.update { it.copy(selectedBranchId = branchId) }
    }

    fun verifyEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            findUserByEmailUseCase(uiState.value.emailQuery)
                .onSuccess { user ->
                    if (user != null) {
                        _uiState.update { it.copy(isLoading = false, foundUser = user) }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "User not found.")
                        }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, error = "Error searching for user.") }
                }
        }
    }
    fun addStaff(role: String) {
        val userToAdd = uiState.value.foundUser ?: return
        val departmentId = uiState.value.selectedDepartmentId
        val branchId = uiState.value.selectedBranchId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // <-- Show loading
            addStaffUseCase(userToAdd, currentBusinessId, role, departmentId, branchId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false, // <-- Hide loading
                            infoMessage = "${userToAdd.fullName} added successfully!",
                            emailQuery = "",
                            foundUser = null,
                            selectedRole = "",
                            selectedDepartmentId = "",
                            selectedBranchId = ""
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false, // <-- Hide loading
                            error = "Failed to add staff member."
                        )
                    }
                }
        }
    }
}