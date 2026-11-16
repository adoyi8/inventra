package com.sundroid.kobokobo.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.inventory.data.domain.model.StockMovement
import com.sundroid.kobokobo.inventory.data.domain.model.MovementType
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.inventory.data.domain.model.Location
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateStockMovementUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetLocationsUseCase
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockTransferUiState(
    val selectedProductId: String = "",
    val selectedFromLocationId: String = "",
    val selectedToLocationId: String = "",
    val quantity: String = "",
    val reason: String = "",
    val products: List<Product> = emptyList(),
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class StockTransferViewModel(
    private val createStockMovementUseCase: CreateStockMovementUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getLocationsUseCase: GetLocationsUseCase,
    private val businessId: String,
    private val staffId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StockTransferUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load products and locations in parallel
            launch {
                getProductsUseCase(businessId)
                    .catch { error ->
                        _uiState.update { 
                            it.copy(errorMessage = "Failed to load products: ${error.message}")
                        }
                    }
                    .collect { products ->
                        _uiState.update { it.copy(products = products) }
                    }
            }
            
            launch {
                getLocationsUseCase(businessId)
                    .catch { error ->
                        _uiState.update { 
                            it.copy(errorMessage = "Failed to load locations: ${error.message}")
                        }
                    }
                    .collect { locations ->
                        _uiState.update { 
                            it.copy(
                                locations = locations,
                                isLoading = false
                            )
                        }
                    }
            }
        }
    }

    fun updateSelectedProduct(productId: String) {
        _uiState.update { it.copy(selectedProductId = productId) }
    }

    fun updateSelectedFromLocation(locationId: String) {
        _uiState.update { it.copy(selectedFromLocationId = locationId) }
    }

    fun updateSelectedToLocation(locationId: String) {
        _uiState.update { it.copy(selectedToLocationId = locationId) }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
    }

    fun updateReason(reason: String) {
        _uiState.update { it.copy(reason = reason) }
    }

    fun transferStock() {
        val currentState = _uiState.value
        
        if (currentState.selectedProductId.isBlank() || 
            currentState.selectedToLocationId.isBlank() || 
            currentState.quantity.isBlank()) {
            _uiState.update { 
                it.copy(errorMessage = "Please fill in all required fields")
            }
            return
        }

        val quantity = currentState.quantity.toDoubleOrNull()
        if (quantity == null || quantity <= 0) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter a valid quantity")
            }
            return
        }

        if (currentState.selectedFromLocationId == currentState.selectedToLocationId) {
            _uiState.update { 
                it.copy(errorMessage = "From and To locations cannot be the same")
            }
            return
        }

        val movement = StockMovement(
            productId = currentState.selectedProductId,
            fromLocationId = currentState.selectedFromLocationId.takeIf { it.isNotBlank() },
            toLocationId = currentState.selectedToLocationId,
            quantity = quantity,
            movementType = MovementType.TRANSFER,
            businessId = businessId,
            staffId = staffId,
            reason = currentState.reason
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            createStockMovementUseCase(movement)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Stock transferred successfully!",
                            selectedProductId = "",
                            selectedFromLocationId = "",
                            selectedToLocationId = "",
                            quantity = "",
                            reason = ""
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to transfer stock: ${error}"
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









