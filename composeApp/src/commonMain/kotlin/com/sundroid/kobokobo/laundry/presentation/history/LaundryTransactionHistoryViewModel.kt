package com.sundroid.kobokobo.laundry.presentation.history

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryTransaction
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryTransactionRepository
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LaundryHistoryState(
    val isLoading: Boolean = false,
    val allTransactions: List<LaundryTransaction> = emptyList(),
    val filteredTransactions: List<LaundryTransaction> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val staffList: List<Staff> = emptyList(),
    val showProgressDialog: Boolean = false,
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val showReceipt: Boolean = false,
    val selectedTransaction: LaundryTransaction? = null,
    
    // Pagination attributes
    val page: Long = 1,
    val limit: Long = 10,
    val totalPages: Long = 1,
    val totalOrders: Long = 0
)

class LaundryTransactionHistoryViewModel(
    private val repository: LaundryTransactionRepository,
    private val businessRepository: BusinessRepository,
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(LaundryHistoryState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var transactionJob: Job? = null
    private var currentFilterRequest = PaginationRequest()

    init {
        fetchStaffList()
        getPaymentMethods()
        observeTransactions()
    }

    private fun observeTransactions() {
        if (businessId.isEmpty()) return
        _state.update { it.copy(isLoading = true) }
        transactionJob?.cancel()
        transactionJob = repository.getLaundryTransactions(businessId)
            .onEach { transactions ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        allTransactions = transactions
                    )
                }
                applyFilters(currentFilterRequest)
            }
            .launchIn(viewModelScope)
    }

    fun applyFilters(request: PaginationRequest) {
        currentFilterRequest = request
        val allTx = _state.value.allTransactions
        
        var filtered = allTx.filter { tx ->
            var match = true
            
            // Payment method
            if (request.payment_method != null && request.payment_method != "All") {
                if (tx.paymentMethod != request.payment_method) {
                    match = false
                }
            }

            // Staff
            if (request.staff_user_id != null && request.staff_user_id != "All") {
                if (tx.staffId != request.staff_user_id) {
                    match = false
                }
            }
            
            // Date Range
            if (request.start_date != null && request.end_date != null) {
                try {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
                    val startMillis = sdf.parse(request.start_date!!)?.time ?: 0L
                    val endMillis = sdf.parse(request.end_date!!)?.time ?: Long.MAX_VALUE
                    
                    val txMillis = tx.timestamp
                    if (txMillis < startMillis || txMillis > (endMillis + 86400000L)) { // Add 1 day to make end date inclusive
                        match = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            match
        }

        // Apply Pagination
        val limit = request.limit
        val page = request.page
        val totalPages = (filtered.size + limit - 1) / limit
        
        val startIndex = ((page - 1) * limit).toInt().coerceAtLeast(0)
        val endIndex = (startIndex + limit.toInt()).coerceAtMost(filtered.size)
        
        val pagedTransactions = if (startIndex < filtered.size) {
            filtered.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        _state.update { 
            it.copy(
                filteredTransactions = pagedTransactions,
                page = page,
                limit = limit.toLong(),
                totalPages = totalPages.toLong(),
                totalOrders = filtered.size.toLong()
            )
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

    private fun getPaymentMethods() {
        if (businessId.isEmpty()) return
        viewModelScope.launch {
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    _state.update { it.copy(paymentMethods = methods) }
                }
        }
    }

    fun viewReceipt(transaction: LaundryTransaction) {
        _state.update { it.copy(showReceipt = true, selectedTransaction = transaction) }
    }

    fun hideReceipt() {
        _state.update { it.copy(showReceipt = false, selectedTransaction = null) }
    }

    fun exportTransactions(
        businessName: String,
        staffName: String,
        paymentMethod: String,
        dateRange: String,
        pageStr: String,
        transactions: List<LaundryTransaction>,
        pdfExporter: com.sundroid.kobokobo.utils.PdfExporter
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val orderModels = transactions.map { tx ->
                val methodText = _state.value.paymentMethods.find { it.id == tx.paymentMethod }?.name ?: tx.paymentMethod
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                val dateString = try { dateFormat.format(java.util.Date(tx.timestamp)) } catch(e: Exception) { "Unknown" }

                val items = tx.items.map { item ->
                    com.sundroid.kobokobo.cart.domain.CartItem(
                        itemId = item.materialId,
                        itemName = "${item.materialName} - ${item.serviceType}",
                        quantity = item.quantity,
                        price = item.unitPrice,
                        costPrice = 0.0,
                        vatRate = 0.0,
                        discount = 0.0,
                        image = ""
                    )
                }

                com.sundroid.kobokobo.transactionHistory.domain.OrderModel(
                    orderId = tx.reference,
                    created = dateString,
                    paymentMethod = tx.paymentMethod,
                    totalAmount = tx.totalAmount.toString(),
                    paymentMethodText = methodText,
                    items = items
                )
            }

            val exportResult = pdfExporter.exportTransactionPdf(
                businessName = businessName,
                staffName = staffName,
                paymentMethod = paymentMethod,
                dateRange = dateRange,
                pageStr = pageStr,
                transactions = orderModels
            )

            _state.update { it.copy(isLoading = false) }

            if (exportResult.openTarget != null) {
                // Show snackbar with action
                val snackbarResult = _state.value.snackBarHostState.showSnackbar(
                    message = exportResult.message,
                    actionLabel = "Open",
                    duration = androidx.compose.material3.SnackbarDuration.Long
                )
                if (snackbarResult == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                    val openError = pdfExporter.openPdf(exportResult.openTarget)
                    if (openError != null) {
                        _state.value.snackBarHostState.showSnackbar(openError)
                    }
                }
            } else {
                _state.value.snackBarHostState.showSnackbar(exportResult.message)
            }
        }
    }
}
