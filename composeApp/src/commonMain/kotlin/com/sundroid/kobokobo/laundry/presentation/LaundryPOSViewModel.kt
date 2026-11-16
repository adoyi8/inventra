package com.sundroid.kobokobo.laundry.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryCartItem
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryTransaction
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryTransactionRepository
import com.sundroid.kobokobo.dashboard.utils.Util
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LaundryPOSState(
    val cartItems: List<LaundryCartItem> = emptyList(),
    val isCheckoutLoading: Boolean = false,
    val showCartSheet: Boolean = false,
    val showCheckoutSuccess: Boolean = false,
    val checkoutError: String? = null,
    val transactionReference: String = ""
) {
    val totalAmount: Double
        get() = cartItems.sumOf { it.getSubTotal() }
}

class LaundryPOSViewModel(
    private val transactionRepository: LaundryTransactionRepository,
    private val businessId: String,
    private val staffId: String,
    private val staffName: String
) : ViewModel() {

    private val _state = MutableStateFlow(LaundryPOSState())
    val state: StateFlow<LaundryPOSState> = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        _state.update { it.copy(transactionReference = Util.generateTransactionReference()) }
    }

    fun addItemToCart(item: LaundryCartItem) {
        val currentItems = _state.value.cartItems.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { 
            it.materialId == item.materialId && it.serviceType == item.serviceType 
        }

        if (existingItemIndex != -1) {
            val existing = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            currentItems.add(item)
        }

        _state.update { it.copy(cartItems = currentItems) }
    }

    fun updateItemQuantity(item: LaundryCartItem, newQuantity: Int) {
        val currentItems = _state.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { 
            it.materialId == item.materialId && it.serviceType == item.serviceType 
        }

        if (index != -1) {
            if (newQuantity <= 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = item.copy(quantity = newQuantity)
            }
            _state.update { it.copy(cartItems = currentItems) }
        }
    }

    fun clearCart() {
        _state.update { 
            it.copy(
                cartItems = emptyList(),
                transactionReference = Util.generateTransactionReference()
            ) 
        }
    }

    fun toggleCartSheet(show: Boolean) {
        _state.update { it.copy(showCartSheet = show) }
    }

    fun checkout(paymentMethod: String) {
        if (_state.value.cartItems.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isCheckoutLoading = true, checkoutError = null) }
            
            val transaction = LaundryTransaction(
                reference = _state.value.transactionReference,
                staffId = staffId,
                staffName = staffName,
                items = _state.value.cartItems,
                totalAmount = _state.value.totalAmount,
                paymentMethod = paymentMethod
            )

            when (val result = transactionRepository.postLaundryTransaction(businessId, transaction)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isCheckoutLoading = false,
                            showCheckoutSuccess = true,
                            showCartSheet = false,
                            cartItems = emptyList(),
                            transactionReference = Util.generateTransactionReference()
                        ) 
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isCheckoutLoading = false,
                            checkoutError = result.error.toString()
                        ) 
                    }
                }
            }
        }
    }

    fun dismissSuccessDialog() {
        _state.update { it.copy(showCheckoutSuccess = false) }
    }
    
    fun clearError() {
        _state.update { it.copy(checkoutError = null) }
    }
}
