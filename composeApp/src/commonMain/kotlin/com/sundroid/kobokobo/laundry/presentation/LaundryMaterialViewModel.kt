package com.sundroid.kobokobo.laundry.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryMaterial
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LaundryState(
    val materials: List<LaundryMaterial> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LaundryMaterialViewModel(
    private val laundryRepository: LaundryRepository,
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(LaundryState())
    val state: StateFlow<LaundryState> = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        loadMaterials()
    }

    private fun loadMaterials() {
        _state.update { it.copy(isLoading = true) }
        laundryRepository.getLaundryMaterials(businessId)
            .onEach { materials ->
                _state.update { it.copy(materials = materials, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)
    }

    fun addMaterial(name: String, washingPrice: Double, ironingPrice: Double, washAndIronPrice: Double, staffId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val material = LaundryMaterial(
                name = name,
                washingPrice = washingPrice,
                ironingPrice = ironingPrice,
                washAndIronPrice = washAndIronPrice,
                createdBy = staffId,
                updatedBy = staffId
            )
            when (val result = laundryRepository.addLaundryMaterial(businessId, material)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }
            }
        }
    }

    fun updateMaterial(material: LaundryMaterial, staffId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val updated = material.copy(
                updatedAt = System.currentTimeMillis(),
                updatedBy = staffId
            )
            when (val result = laundryRepository.updateLaundryMaterial(businessId, updated)) {
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
            when (val result = laundryRepository.deleteLaundryMaterial(businessId, id)) {
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
