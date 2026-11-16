package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoryByIdUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// --- REFACTORED UI STATE ---
data class CategoryFormUiState(
    val categoryId: String? = null, // Null in 'Add' mode, set in 'Edit' mode
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val isInitialLoad: Boolean = true, // To show loading only on first fetch for 'Edit'
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isDeleted: Boolean = false, // Added for delete functionality
    val isSaved: Boolean = false
)

// --- REFACTORED VIEWMODEL ---
class CategoryFormViewModel(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase, // New
    private val updateCategoryUseCase: UpdateCategoryUseCase,   // New
    private val deleteCategoryUseCase: DeleteCategoryUseCase,   // New
    private val businessId: String,
    private val staffId: String,
    private val categoryId: String? = null

) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState(categoryId = categoryId))
    val uiState = _uiState.asStateFlow()

    private val isEditMode: Boolean get() = categoryId != null

    init {
        if (isEditMode) {
            loadCategory()
        } else {
            // Immediately stop initial loading for 'Add' mode
            _uiState.update { it.copy(isInitialLoad = false) }
        }
    }


    // REVISED loadCategory function
    private fun loadCategory() {
        if (categoryId == null) return // This correctly prevents the call if no ID exists

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getCategoryByIdUseCase(categoryId=categoryId, businessId = businessId)
                .onSuccess { category ->
                    // Null-check the category object
                    if (category != null) {
                        _uiState.update {
                            it.copy(
                                // Ensure id and name are non-null or handle if they are nullable
                                categoryId = category.id,
                                name = category.name,
                                description = category.description,
                                isLoading = false,
                                isInitialLoad = false
                            )
                        }
                    } else {
                        // Handle 'not found' case as an error for the user
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isInitialLoad = false,
                                errorMessage = "Category not found."
                            )
                        }
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isInitialLoad = false,
                            errorMessage = "Failed to load category: ${error.toString()}"
                        )
                    }
                }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveCategory() {
        if (isEditMode) {
            updateCategory()
        } else {
            createCategory()
        }
    }

    // --- Core Logic ---

    private fun createCategory() {
        val currentState = _uiState.value

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a category name") }
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
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            createCategoryUseCase(category)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Category created successfully!",
                            isSaved = true,
                            name = "", // Clear fields after successful create
                            description = ""
                        )
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to create category: ${error.toString()}"
                        )
                    }
                }
        }
    }

    private fun updateCategory() {
        val currentState = _uiState.value
        val id = currentState.categoryId ?: return

        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a category name") }
            return
        }

        // NOTE: In a real app, you would need the existing Category object to preserve
        // `createdBy` and other immutable fields. Assuming `updateCategoryUseCase` handles this.
        val updatedCategory = Category(
            id = id,
            name = currentState.name,
            description = currentState.description,
            businessId = businessId, // Re-use businessId
            createdBy = "", // Placeholder: should be original value
            updatedBy = staffId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            updateCategoryUseCase(updatedCategory)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            successMessage = "Category updated successfully!",
                            isSaved = true
                        )
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to update category: ${error.toString()}"
                        )
                    }
                }
        }
    }

    fun deleteCategory() {
        val id = _uiState.value.categoryId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            deleteCategoryUseCase(id, businessId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isDeleted = true, // Flag for navigation
                            successMessage = "Category deleted successfully!"
                        )
                    }
                }
                .onError { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to delete category: ${error.toString()}"
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