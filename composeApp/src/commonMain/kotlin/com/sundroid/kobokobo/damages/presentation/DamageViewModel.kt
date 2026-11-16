package com.sundroid.kobokobo.damages.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.damages.data.domain.CreateDamageUseCase
import com.sundroid.kobokobo.damages.data.domain.Damage
import com.sundroid.kobokobo.damages.data.domain.GetDamagesUseCase
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DamagesViewModel(
    private val getDamagesUseCase: GetDamagesUseCase,
    private val createDamageUseCase: CreateDamageUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val businessId: String,
    private val staffId: String,
    private val staffName: String
) : ViewModel() {

    private val _state = MutableStateFlow(DamageState())
    val state: StateFlow<DamageState> = _state.asStateFlow()

    init {
        loadDamages()
        loadProducts()
    }

    private fun loadDamages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingList = true) }
            getDamagesUseCase(businessId)
                .catch { error ->
                    _state.update { it.copy(isLoadingList = false, listErrorMessage = error.message) }
                }
                .collect { damages ->
                    _state.update { it.copy(damages = damages, isLoadingList = false) }
                }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            // Get all products for the business
            getProductsUseCase(businessId)
                .collect { products ->
                    _state.update {
                        it.copy(
                            allProducts = products,
                            filteredProducts = products
                        )
                    }
                }
        }
    }

    // --- Dialog Actions ---

    fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _state.update {
            it.copy(
                showAddDialog = false,
                // Reset form fields
                searchQuery = "",
                filteredProducts = it.allProducts,
                selectedProduct = null,
                quantity = "",
                reason = "",
                submitErrorMessage = null,
                isSubmitSuccess = false
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredProducts = if (query.isBlank()) {
                    it.allProducts
                } else {
                    it.allProducts.filter { product ->
                        product.name.contains(query, ignoreCase = true) ||
                                product.sku.contains(query, ignoreCase = true)
                    }
                }
            )
        }
    }

    fun selectProduct(product: Product?) {
        _state.update { it.copy(selectedProduct = product, searchQuery = product?.name ?: "") }
    }

    fun onQuantityChanged(quantity: String) {
        if (quantity.isEmpty() || quantity.all { it.isDigit() }) {
            _state.update { it.copy(quantity = quantity) }
        }
    }

    fun onReasonChanged(reason: String) {
        _state.update { it.copy(reason = reason) }
    }

    fun submitDamage() {
        val currentState = state.value
        if (!currentState.canSubmit) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitErrorMessage = null) }

            val damage = Damage(
                businessId = businessId,
                productId = currentState.selectedProduct!!.id,
                productName = currentState.selectedProduct.name,
                quantity = currentState.quantity.toInt(),
                costPrice = currentState.selectedProduct.costPrice,
                staffId = staffId,
                staffName = staffName,
                reason = currentState.reason
            )

            when (val result = createDamageUseCase(damage)) {
                is Result.Success -> {
                    _state.update { it.copy(isSubmitting = false, isSubmitSuccess = true) }
                    // The list will automatically update via the Flow from getDamagesUseCase
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(isSubmitting = false, submitErrorMessage = "Failed to submit: ${result.error}")
                    }
                }
            }
        }
    }

    fun resetSubmitSuccess() {
        _state.update { it.copy(isSubmitSuccess = false) }
    }
}