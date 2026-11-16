package com.sundroid.kobokobo.services.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.services.data.domain.model.ServicesMaterial
import com.sundroid.kobokobo.services.data.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServicesState(
    val materials: List<ServicesMaterial> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ServicesMaterialViewModel(
    private val servicesRepository: ServicesRepository,
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesState())
    val state: StateFlow<ServicesState> = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        loadMaterials()
    }

    private fun loadMaterials() {
        _state.update { it.copy(isLoading = true) }
        servicesRepository.getServicesMaterials(businessId)
            .onEach { materials ->
                _state.update { it.copy(materials = materials, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)
    }

    fun addMaterial(name: String, price: Double, staffId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val material = ServicesMaterial(
                name = name,
                price = price,
                createdBy = staffId,
                updatedBy = staffId
            )
            when (val result = servicesRepository.addServicesMaterial(businessId, material)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }
            }
        }
    }

    fun updateMaterial(material: ServicesMaterial, staffId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val updated = material.copy(
                updatedAt = System.currentTimeMillis(),
                updatedBy = staffId
            )
            when (val result = servicesRepository.updateServicesMaterial(businessId, updated)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }
            }
        }
    }

    fun deleteMaterial(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = servicesRepository.deleteServicesMaterial(businessId, id)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
