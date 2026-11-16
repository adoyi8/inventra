package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateCategoryUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddCategoryUiState(
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AddCategoryViewModel(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val businessId: String,
    private val staffId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun createCategory() {
        val currentState = _uiState.value
        
        if (currentState.name.isBlank()) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter a category name")
            }
            return
        }

        val category = Category(
            name = currentState.name,
            description = currentState.description,
            businessId = businessId,
            createdBy = staffId,
            updatedBy = staffId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            createCategoryUseCase(category)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Category created successfully!",
                            name = "",
                            description = ""
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to create category: ${error.toString()}"
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









