package com.sundroid.kobokobo.department

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.admin.data.domain.model.Department
import com.sundroid.kobokobo.admin.data.domain.useCase.CreateDepartmentUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.DeleteDepartmentUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetDepartmentsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.UpdateDepartmentUseCase
import com.sundroid.kobokobo.login.presentation.UserViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import com.sundroid.kobokobo.core.domain.Result
// Assuming 'Department', 'UserViewModel', 'Result', 'DataError'
// and your UseCases are available in this scope

class DepartmentViewModel(
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val createDepartmentUseCase: CreateDepartmentUseCase,
    private val updateDepartmentUseCase: UpdateDepartmentUseCase,
    private val deleteDepartmentUseCase: DeleteDepartmentUseCase,
    private val userViewModel: UserViewModel // Injected to get the businessId
) : ViewModel() {

    // UI event flow for navigation and errors
    sealed interface UiEvent {
        data object SaveSuccess : UiEvent
        data class Error(val message: String) : UiEvent
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // State for the currently selected department (for create/edit)
    private val _selectedDepartment = MutableStateFlow<Department?>(null)
    val selectedDepartment: StateFlow<Department?> = _selectedDepartment.asStateFlow()

    // Flag to indicate if we are editing an existing department
    val isEditing: StateFlow<Boolean> = _selectedDepartment.map { it != null }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)

    // Flow of departments for the current business
    val departments: StateFlow<List<Department>> = userViewModel.state
        .map { it.currentUser?.businessId }
        .flatMapLatest { businessId ->
            if (businessId.isNullOrEmpty()) {
                flowOf(emptyList())
            } else {
                // This calls your GetDepartmentsUseCase
                getDepartmentsUseCase(businessId)
            }
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Public Functions ---

    fun startCreating() {
        _selectedDepartment.value = null
    }

    fun startEditing(department: Department) {
        _selectedDepartment.value = department
    }

    fun deleteDepartment(department: Department) = viewModelScope.launch {
        val businessId = userViewModel.state.value.currentUser?.businessId
        if (businessId.isNullOrEmpty()) {
            _eventFlow.emit(UiEvent.Error("User not associated with a business."))
            return@launch
        }

        // This calls your DeleteDepartmentUseCase
        when (deleteDepartmentUseCase(department.id, businessId)) {
            is Result -> _eventFlow.emit(UiEvent.Error("Failed to delete department."))
            is Result.Success -> { /* UI will update via the flow */ }
        }
    }

    fun saveDepartment(name: String, description: String) = viewModelScope.launch {
        val businessId = userViewModel.state.value.currentUser?.businessId
        if (businessId.isNullOrEmpty()) {
            _eventFlow.emit(UiEvent.Error("User not associated with a business."))
            return@launch
        }
        if (name.isBlank()) {
            _eventFlow.emit(UiEvent.Error("Department name cannot be empty."))
            return@launch
        }

        val currentDept = _selectedDepartment.value
        val result = if (currentDept == null) {
            // Create new
            val newDepartment = Department(
                id = UUID.randomUUID().toString(),
                businessId = businessId,
                name = name.trim(),
                description = description.trim()
            )
            // This calls your CreateDepartmentUseCase
            createDepartmentUseCase(newDepartment)
        } else {
            // Update existing
            val updatedDepartment = currentDept.copy(
                name = name.trim(),
                description = description.trim()
            )
            // This calls your UpdateDepartmentUseCase
            updateDepartmentUseCase(updatedDepartment)
        }

        when (result) {
            is Result.Success -> _eventFlow.emit(UiEvent.SaveSuccess)
            is Result.Error -> _eventFlow.emit(UiEvent.Error("Failed to save department."))
        }
    }
}