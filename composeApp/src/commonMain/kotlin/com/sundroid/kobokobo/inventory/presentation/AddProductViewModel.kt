package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoriesUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddProductUiState(
    val name: String = "",
    val unitPrice: String = "",
    val sku: String = "",
    val description: String = "",
    val discount: String = "",
    val vat: String = "0.0",
    val selectedCategoryId: String = "",
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AddProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val businessId: String,
    private val staffId: String
) : ViewModel()
{
    
    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase(businessId)
                .catch { error ->
                    _uiState.update { 
                        it.copy(errorMessage = "Failed to load categories: ${error.message}")
                    }
                }
                .collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateUnitPrice(unitPrice: String) {
        _uiState.update { it.copy(unitPrice = unitPrice) }
    }

    fun updateSku(sku: String) {
        _uiState.update { it.copy(sku = sku) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateDiscount(discount: String) {
        _uiState.update { it.copy(discount = discount) }
    }

    fun updateVat(vat: String) {
        _uiState.update { it.copy(vat = vat) }
    }

    fun updateSelectedCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun createProduct() {
        val currentState = _uiState.value
        
        if (currentState.name.isBlank() || currentState.unitPrice.isBlank() || currentState.sku.isBlank()) {
            _uiState.update { 
                it.copy(errorMessage = "Please fill in all required fields")
            }
            return
        }

        val unitPrice = currentState.unitPrice.toDoubleOrNull()
        if (unitPrice == null || unitPrice <= 0) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter a valid unit price")
            }
            return
        }

        val discount = currentState.discount.toDoubleOrNull()
        val vat = currentState.vat.toDoubleOrNull() ?: 0.0

        val product = Product(
            name = currentState.name,
            unitPrice = unitPrice,
            sku = currentState.sku,
            description = currentState.description,
            discount = discount,
            vat = vat,
            categoryId = currentState.selectedCategoryId,
            businessId = businessId,
            createdBy = staffId,
            updatedBy = staffId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            createProductUseCase(product)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Product created successfully!",
                            name = "",
                            unitPrice = "",
                            sku = "",
                            description = "",
                            discount = "",
                            vat = "0.0",
                            selectedCategoryId = ""
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to create product: ${error}"
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









