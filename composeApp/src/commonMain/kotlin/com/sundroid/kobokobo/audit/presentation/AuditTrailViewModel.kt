
package com.sundroid.kobokobo.audit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailPaginationRequest
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuditTrailViewModel(
    private val auditTrailRepository: AuditTrailRepository,
    private val businessRepository: BusinessRepository,
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(AuditTrailState())
    val state = _state.asStateFlow()

    init {
        fetchStaffList()
        // Initial load with default request
        getAuditTrails(AuditTrailPaginationRequest())
    }

    fun getAuditTrails(paginationRequest: AuditTrailPaginationRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "") }
            auditTrailRepository.getAuditTrails(businessId, paginationRequest)
                .onSuccess { dto ->
                    _state.update {
                        it.copy(
                            auditTrails = dto.auditTrails,
                            totalRecords = dto.totalRecords,
                            totalPages = dto.totalPages,
                            limit = dto.limit,
                            page = dto.page,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toString()) }
                }
        }
    }

    fun applyFilters(newRequest: AuditTrailPaginationRequest) {
        // Reset to page 1 when new filters are applied
        newRequest.page = 1
        getAuditTrails(newRequest)
    }

    fun setProductId(productId: String) {
        val request = AuditTrailPaginationRequest(productId = productId)
        getAuditTrails(request)
    }

    private fun fetchStaffList() {
        viewModelScope.launch {
            businessRepository.getStaffForBusiness(businessId)
                .onSuccess { list ->
                    _state.update { it.copy(staffList = list) }
                }
        }
    }

    fun hideAllDialogs() {
        TODO("Not yet implemented")
    }
}

