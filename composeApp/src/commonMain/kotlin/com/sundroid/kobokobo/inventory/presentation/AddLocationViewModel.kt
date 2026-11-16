package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Location
import com.sundroid.kobokobo.inventory.data.domain.model.LocationType
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateLocationUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddLocationUiState(
    val name: String = "",
    val description: String = "",
    val selectedLocationType: LocationType = LocationType.STORE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AddLocationViewModel(
    private val createLocationUseCase: CreateLocationUseCase,
    private val businessId: String,
    private val staffId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddLocationUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateLocationType(locationType: LocationType) {
        _uiState.update { it.copy(selectedLocationType = locationType) }
    }

    fun createLocation() {
        val currentState = _uiState.value
        
        if (currentState.name.isBlank()) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter a location name")
            }
            return
        }

        val location = Location(
            name = currentState.name,
            description = currentState.description,
            businessId = businessId,
            locationType = currentState.selectedLocationType,
            createdBy = staffId,
            updatedBy = staffId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            createLocationUseCase(location)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Location created successfully!",
                            name = "",
                            description = "",
                            selectedLocationType = LocationType.STORE
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to create location: ${error}"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}









