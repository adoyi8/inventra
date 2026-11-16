package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoriesUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteCategoryUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryListUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CategoryListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val businessId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getCategoriesUseCase(businessId)
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load categories: ${error.message}"
                        )
                    }
                }
                .collect { categories ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            categories = categories
                        )
                    }
                }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            deleteCategoryUseCase(categoryId, businessId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadCategories() // Refresh the list
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete category: ${error}"
                        )
                    }
                }
        }
    }

    fun refreshCategories() {
        loadCategories()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}









