package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Location
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetLocationsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteLocationUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationListUiState(
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LocationListViewModel(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val businessId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LocationListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getLocationsUseCase(businessId)
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load locations: ${error.message}"
                        )
                    }
                }
                .collect { locations ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            locations = locations
                        )
                    }
                }
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            deleteLocationUseCase(locationId, businessId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadLocations() // Refresh the list
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete location: ${error}"
                        )
                    }
                }
        }
    }

    fun refreshLocations() {
        loadLocations()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}









