package com.sundroid.kobokobo.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentMethodsState(
    val methods: List<PaymentMethod> = emptyList(),
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val editingMethod: PaymentMethod? = null,
    val methodName: String = "",
    val methodActive: Boolean = true,
    val error: String? = null
)

class PaymentMethodsViewModel(
    private val businessRepository: BusinessRepository,
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodsState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PaymentMethodsState()
        )

    init {
        fetchPaymentMethods()
    }

    fun fetchPaymentMethods() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    _state.update { it.copy(methods = methods, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.toString(), isLoading = false) }
                }
        }
    }

    fun onAddMethodClick() {
        _state.update { it.copy(showDialog = true, editingMethod = null, methodName = "", methodActive = true) }
    }

    fun onEditMethodClick(method: PaymentMethod) {
        _state.update { it.copy(showDialog = true, editingMethod = method, methodName = method.name, methodActive = method.active) }
    }

    fun onDismissDialog() {
        _state.update { it.copy(showDialog = false, editingMethod = null) }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(methodName = name) }
    }

    fun onActiveChange(active: Boolean) {
        _state.update { it.copy(methodActive = active) }
    }

    fun onSaveMethod() {
        val currentState = _state.value
        val method = if (currentState.editingMethod != null) {
            currentState.editingMethod.copy(name = currentState.methodName, active = currentState.methodActive)
        } else {
            // Generate a simple ID or use firestore auto-id logic if possible.
            // Since we use set() in repo with a provided ID, we need to generate one here.
            // For simplicity, let's use a timestamp based ID or just let Repository handle ID generation if we change logic.
            // But implementing logic "document()" in repo generates ID. 
            // Wait, DefaultBusinessRepository.addPaymentMethod uses document(method.id).set(method). 
            // So we MUST provide an ID here.
            PaymentMethod(
                id = "pm_${System.currentTimeMillis()}", // Simple unique ID generation
                name = currentState.methodName,
                businessId = businessId,
                active = currentState.methodActive
            )
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (currentState.editingMethod != null) {
                businessRepository.updatePaymentMethod(businessId, method)
            } else {
                businessRepository.addPaymentMethod(businessId, method)
            }

            result.onSuccess {
                fetchPaymentMethods() // Refresh list
                _state.update { it.copy(showDialog = false, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(error = "Failed to save: $error", isLoading = false) }
            }
        }
    }

    fun onDeleteMethod(method: PaymentMethod) {
         viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            businessRepository.deletePaymentMethod(businessId, method.id)
                .onSuccess {
                    fetchPaymentMethods()
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                     _state.update { it.copy(error = "Failed to delete: $error", isLoading = false) }
                }
         }
    }
}
