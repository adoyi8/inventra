package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteProductUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val businessId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            getProductsUseCase(businessId)
                .catch { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load products: ${error.message}"
                        )
                    }
                }
                .collect { products ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            products = products
                        )
                    }
                }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            deleteProductUseCase(productId, businessId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadProducts() // Refresh the list
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete product: ${error}"
                        )
                    }
                }
        }
    }

    fun refreshProducts() {
        loadProducts()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}









