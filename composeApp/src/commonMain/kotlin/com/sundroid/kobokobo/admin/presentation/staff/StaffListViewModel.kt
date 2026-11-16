package com.sundroid.kobokobo.admin.presentation.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.Staff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffListUiState(
    val isLoading: Boolean = false,
    val staff: List<Staff> = emptyList(),
    val errorMessage: String? = null
)

class StaffListViewModel(
    private val businessRepository: BusinessRepository,
    private val businessId: String?
) : ViewModel() {
    private val _uiState = MutableStateFlow(StaffListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val id = businessId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            businessRepository.getStaffForBusiness(id)
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, staff = list) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load staff") }
                }
        }
    }
}



