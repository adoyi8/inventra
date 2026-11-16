package com.sundroid.kobokobo.services.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.services.data.domain.model.ServicesCartItem
import com.sundroid.kobokobo.services.data.domain.model.ServicesTransaction
import com.sundroid.kobokobo.services.data.domain.repository.ServicesTransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServicesPOSState(
    val cartItems: List<ServicesCartItem> = emptyList(),
    val isCheckoutLoading: Boolean = false,
    val showCartSheet: Boolean = false,
    val showCheckoutSuccess: Boolean = false,
    val checkoutError: String? = null,
    val transactionReference: String = "",
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: String = "",
    val selectedPaymentMethodText: String = "",
    val proceedWithoutPayment: Boolean = false,
    val staffList: List<Staff> = emptyList(),
    val assignedStaffId: String? = null,
    val assignedStaffName: String? = null
) {
    val totalAmount: Double
        get() = cartItems.sumOf { it.getSubTotal() }
}

class ServicesPOSViewModel(
    private val transactionRepository: ServicesTransactionRepository,
    private val businessRepository: BusinessRepository,
    private val businessId: String,
    private val staffId: String,
    private val staffName: String
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesPOSState())
    val state: StateFlow<ServicesPOSState> = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    init {
        _state.update { it.copy(transactionReference = Util.generateTransactionReference()) }
        getPaymentMethods()
        fetchStaffList()
    }

    private fun getPaymentMethods() {
        if (businessId.isEmpty()) return
        viewModelScope.launch {
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    _state.update {
                        it.copy(
                            paymentMethods = methods,
                            selectedPaymentMethod = it.selectedPaymentMethod,
                            selectedPaymentMethodText = it.selectedPaymentMethodText
                        )
                    }
                }
        }
    }

    private fun fetchStaffList() {
        if (businessId.isEmpty()) return
        viewModelScope.launch {
            businessRepository.getStaffForBusiness(businessId)
                .onSuccess { list ->
                    _state.update { it.copy(staffList = list) }
                }
        }
    }

    fun setPaymentMethod(paymentMethodId: String) {
        val methodObj = _state.value.paymentMethods.find { it.id == paymentMethodId }
            ?: _state.value.paymentMethods.find { it.name.equals(paymentMethodId, ignoreCase = true) }

        val methodText = methodObj?.name ?: paymentMethodId

        _state.update {
            it.copy(
                selectedPaymentMethod = paymentMethodId,
                selectedPaymentMethodText = methodText,
                proceedWithoutPayment = false
            )
        }
    }

    fun setProceedWithoutPayment(proceedWithoutPayment: Boolean) {
        _state.update {
            it.copy(
                proceedWithoutPayment = proceedWithoutPayment,
                selectedPaymentMethod = if (proceedWithoutPayment) "" else it.selectedPaymentMethod,
                selectedPaymentMethodText = if (proceedWithoutPayment) "" else it.selectedPaymentMethodText
            )
        }
    }

    fun setAssignedStaff(staff: Staff?) {
        _state.update {
            it.copy(
                assignedStaffId = staff?.userId,
                assignedStaffName = staff?.name
            )
        }
    }

    fun addItemToCart(item: ServicesCartItem) {
        val currentItems = _state.value.cartItems.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { 
            it.materialId == item.materialId
        }

        if (existingItemIndex != -1) {
            val existing = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            currentItems.add(item)
        }

        _state.update { it.copy(cartItems = currentItems) }
    }

    fun updateItemQuantity(item: ServicesCartItem, newQuantity: Int) {
        val currentItems = _state.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { 
            it.materialId == item.materialId
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
                transactionReference = Util.generateTransactionReference(),
                proceedWithoutPayment = false
            ) 
        }
    }

    fun toggleCartSheet(show: Boolean) {
        _state.update { it.copy(showCartSheet = show) }
    }

    fun checkout() {
        if (_state.value.cartItems.isEmpty()) return
        val paymentMethod = _state.value.selectedPaymentMethod

        viewModelScope.launch {
            _state.update { it.copy(isCheckoutLoading = true, checkoutError = null) }

            val effectiveStaffId = _state.value.assignedStaffId ?: staffId
            val effectiveStaffName = _state.value.assignedStaffName ?: staffName

            val transaction = ServicesTransaction(
                reference = _state.value.transactionReference,
                businessId = businessId,
                staffId = effectiveStaffId,
                staffName = effectiveStaffName,
                items = _state.value.cartItems,
                totalAmount = _state.value.totalAmount,
                paymentMethod = paymentMethod,
                isPaid = !state.value.proceedWithoutPayment && paymentMethod.isNotBlank(),
                paymentProcessedBy = if (!state.value.proceedWithoutPayment && paymentMethod.isNotBlank()) staffName else null
            )

            when (val result = transactionRepository.postServicesTransaction(businessId, transaction)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isCheckoutLoading = false,
                            showCheckoutSuccess = true,
                            showCartSheet = false,
                            cartItems = emptyList(),
                            transactionReference = Util.generateTransactionReference(),
                            proceedWithoutPayment = false,
                            assignedStaffId = null,
                            assignedStaffName = null
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
