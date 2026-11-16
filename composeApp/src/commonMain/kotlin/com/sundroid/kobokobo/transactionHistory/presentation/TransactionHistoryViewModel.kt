package com.sundroid.kobokobo.transactionHistory.presentation


import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.cart.data.mappers.toReceipt
import com.sundroid.kobokobo.cart.domain.CartRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.transactionHistory.data.mappers.toTransactionHistory
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistory
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistoryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val transactionHistoryRepository: TransactionHistoryRepository,
    private val cartRepository: CartRepository,
    private val businessRepository: BusinessRepository,
    private val businessId: String
): ViewModel()
{
    private val _state = MutableStateFlow(TransactionHistoryState())

    val state = _state.asStateFlow()
        .onStart {
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )
    init {
        fetchStaffList()
        getPaymentMethods()
    }

    fun fetchStaffList(){
        val id = businessId ?: return
        viewModelScope.launch {
            businessRepository.getStaffForBusiness(id)
                .onSuccess { list ->
                    println("abike "+list.size)
                    _state.update { it.copy(staffList = list) }
                }
                .onFailure { e ->
                }
        }
    }

    fun getPaymentMethods() {
        if(businessId.isEmpty()) return
        viewModelScope.launch {
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    _state.update {
                        it.copy(
                            paymentMethods = methods
                        )
                    }
                }
        }
    }


    fun getTransactionHistory(paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            transactionHistoryRepository.getTransactionHistory(businessId, paginationRequest)
                .onSuccess { transactionHistoryResponse ->
                    if(transactionHistoryResponse.responseCode == 0){
                        val history = transactionHistoryResponse.toTransactionHistory()
                        println("angel 3 "+ history.orders?.size)
                        _state.update {
                            it.copy(transactionHistory = history, showProgressDialog = MutableTransitionState(false), page = history.page, limit = history.limit, totalPages = history.totalPages, totalOrders = history.totalOrders)
                        }
                    }
                    else{
                        if(transactionHistoryResponse.responseMessage!!.contains(Util.EXPIRED_TOKEN_MESSAGE, true)) {
                            NavigationViewModel.navController!!.navigate(Route.Login)
                        }
                        _state.update {
                            it.copy(showProgressDialog = MutableTransitionState(false), transactionHistory = TransactionHistory(), backUpHistory = TransactionHistory())
                        }
                        showSnackBar(transactionHistoryResponse.responseMessage.toString())

                    }
                }
                .onError { error->
                    if(error.equals(DataError.Remote.EXPIRED_TOKEN)){
                        NavigationViewModel.navController!!.navigate(Route.Login)
                        return@launch
                    }
                    _state.update {
                        it.copy(showProgressDialog = MutableTransitionState(false))
                    }
                    showSnackBar(error.toString())
                }
        }
    }


    fun searchTransactionHistory(query: String, paginationRequest: MutableState<PaginationRequest>) {
        val newSearchRequest = PaginationRequest(
            keyword = query.takeIf { it.isNotBlank() },
            page = 1,
            limit = paginationRequest.value.limit // Keep the current limit
        )
        // Store this as the new "base" request
        paginationRequest.value = newSearchRequest
        getTransactionHistory(newSearchRequest)
    }
    fun applyFilters(newRequest: PaginationRequest) {
        // Ensure filters are applied from page 1
        newRequest.page = 1
        // Ensure keyword is cleared, as filters don't apply to a keyword search
        newRequest.keyword = null

        println("onyeka 1 "+ newRequest.staff_user_id)
        getTransactionHistory(newRequest)
    }
    fun selectFilter(filter: String, paginationRequest: MutableState<PaginationRequest>) {
        if(filter != "select::") {
            paginationRequest.value.filter_by = filter
        }
        else{
            paginationRequest.value.filter_by = null
        }
        paginationRequest.value.keyword = null
        paginationRequest.value.start_date = null
        paginationRequest.value.end_date = null
        getTransactionHistory(paginationRequest.value)
    }
    fun searchByDateRange(startDate: String, endDate: String, paginationRequest: MutableState<PaginationRequest>) {
        paginationRequest.value.filter_by = null
        paginationRequest.value.start_date = startDate
        paginationRequest.value.end_date = endDate
        getTransactionHistory(paginationRequest.value)
    }
    // --- 👇 NEW FUNCTION TO ADD 👇 ---
    fun selectStaff(userId: String?, paginationRequest: MutableState<PaginationRequest>) {
        paginationRequest.value.staff_user_id = userId
        // Reset other filters
        paginationRequest.value.keyword = null
        paginationRequest.value.start_date = null
        paginationRequest.value.end_date = null
        paginationRequest.value.filter_by = null
        getTransactionHistory(paginationRequest.value)
    }
    public fun hideAllDialogs() {
        _state.update {
            it.copy(showProgressDialog = MutableTransitionState(false))
        }
    }

    fun getReceipt(reference: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            try {

                        cartRepository.getOrderReceipt(
                            VoidOrderRequest(reference, businessId = businessId!!)
                        ).onSuccess { receiptResponseDto ->
                            hideAllDialogs()
                            if (receiptResponseDto!!.responseCode == 0) {
                                val receipt = receiptResponseDto.toReceipt()
                                _state.update {
                                    it.copy(
                                        receiptModel = receipt,
                                        showReceipt = MutableTransitionState(true)
                                    )
                                }
                            } else {
                                hideAllDialogs()
                                showSnackBar(receiptResponseDto.responseMessage.toString())
                            }

                }.onError { response ->

                    hideAllDialogs()
                    if (response.equals(DataError.Remote.EXPIRED_TOKEN)) {
                        NavigationViewModel.navController!!.navigate(Route.Login)
                    }

                }

            } catch (e: Exception) {
                hideAllDialogs()
                showSnackBar(e.message.toString())
            }
        }
    }

    fun deleteSale(reference: String, userId: String, paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            try {
                cartRepository.deleteCartSales(
                    VoidOrderRequest(reference = reference, businessId = businessId, userId = userId)
                ).onSuccess { response ->
                    hideAllDialogs()
                    if (response?.responseCode == 0) {
                        showSnackBar("Sale deleted successfully.")
                        getTransactionHistory(paginationRequest) // Refresh
                    } else {
                        showSnackBar(response?.responseMessage ?: "Failed to delete sale")
                    }
                }.onError { error ->
                    hideAllDialogs()
                    if (error == DataError.Remote.EXPIRED_TOKEN) {
                        NavigationViewModel.navController!!.navigate(Route.Login)
                    } else {
                        showSnackBar(error.toString())
                    }
                }
            } catch (e: Exception) {
                hideAllDialogs()
                showSnackBar(e.message.toString())
            }
        }
    }

    fun markSalePaid(reference: String, paymentMethod: String, paymentMethodText: String, processedBy: String, paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update { it.copy(showProgressDialog = MutableTransitionState(true)) }
            cartRepository.markSalePaid(businessId, reference, paymentMethod, paymentMethodText, processedBy)
                .onSuccess {
                    hideAllDialogs()
                    showSnackBar("Payment recorded")
                    getTransactionHistory(paginationRequest)
                }
                .onError {
                    hideAllDialogs()
                    showSnackBar("Unable to record payment")
                }
        }
    }

    fun toggleShowReceipt(b: Boolean){
        hideAllDialogs()
        _state.update {
            it.copy(
                showReceipt = MutableTransitionState(b)
            )

        }
    }
    fun showSnackBar(
        message: String,
        actionLabel: String? = null,
        onActionPerformed: (() -> Unit)? = null
    ){
        viewModelScope.launch {
            val snackbarResult = state.value.snackBarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
            )

            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onActionPerformed?.invoke()
            }
        }
    }

    fun onShowPageStats() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    showPageStatsDialog = true,
                    isLoadingStats = true,
                    pageStats = null // Reset previous stats
                )
            }

            val orders = state.value.transactionHistory.orders ?: emptyList()
            if (orders.isEmpty()) {
                 _state.update { it.copy(isLoadingStats = false, pageStats = TransactionPageStats()) }
                 return@launch
            }

            val deferredResults = orders.map { order ->
                async {
                    cartRepository.getOrderReceipt(
                        VoidOrderRequest(order.orderId, businessId = businessId)
                    )
                }
            }
            
            val results = deferredResults.awaitAll()
            
            var totalSales = 0.0
            var totalProfit = 0.0
            val itemMap = mutableMapOf<String, Double>()

            results.forEach { result ->
                if (result is com.sundroid.kobokobo.core.domain.Result.Success) {
                     val receipt = result.data?.data
                     // safely sum, handling potential currency formatting like commas
                     val sales = receipt?.summary?.firstOrNull()?.grandTotal?.replace(",","")?.toDoubleOrNull() ?: 0.0
                     val profit = receipt?.summary?.firstOrNull()?.totalProfit?.replace(",","")?.toDoubleOrNull() ?: 0.0
                     
                     totalSales += sales
                     totalProfit += profit
                     
                     receipt?.items?.forEach { item ->
                         val name = item.productName ?: "Unknown"
                         val qty = item.quantity.replace(",","").toDoubleOrNull() ?: 0.0
                         val currentQty = itemMap[name] ?: 0.0
                         itemMap[name] = currentQty + qty
                     }
                }
            }
            
            // Sort items by quantity descending
            val sortedItems = itemMap.entries.sortedByDescending { it.value }.map { 
                ItemBreakdown(it.key, it.value) 
            }

            val stats = TransactionPageStats(
                totalSales = totalSales,
                totalProfit = totalProfit,
                itemBreakdown = sortedItems
            )
            
             _state.update { 
                 it.copy(
                     isLoadingStats = false, 
                     pageStats = stats
                 ) 
             }
        }
    }
    
    fun onHidePageStats() {
        _state.update { it.copy(showPageStatsDialog = false) }
    }

    fun exportTransactionsWithItems(
        businessName: String,
        staffName: String,
        paymentMethod: String,
        dateRange: String,
        pageStr: String,
        rawTransactions: List<OrderModel>,
        pdfExporter: com.sundroid.kobokobo.utils.PdfExporter
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }

            val deferredResults = rawTransactions.map { order ->
                async {
                    cartRepository.getOrderReceipt(
                        VoidOrderRequest(order.orderId, businessId = businessId)
                    )
                }
            }
            
            val results = deferredResults.awaitAll()
            
            val enrichedTransactions = rawTransactions.mapIndexed { index, order ->
                val result = results[index]
                var itemsList: List<com.sundroid.kobokobo.cart.domain.CartItem> = emptyList()
                
                if (result is com.sundroid.kobokobo.core.domain.Result.Success) {
                     itemsList = result.data?.data?.items?.map { dto ->
                         com.sundroid.kobokobo.cart.domain.CartItem(
                             itemId = dto.productId,
                             itemName = dto.productName,
                             quantity = dto.quantity.replace(",","").toDoubleOrNull()?.toInt() ?: 0,
                             price = dto.unitPrice.replace(",","").toDoubleOrNull() ?: 0.0,
                             costPrice = dto.costPrice?.replace(",","")?.toDoubleOrNull() ?: 0.0,
                             vatRate = dto.unitVat.replace(",","").toDoubleOrNull() ?: 0.0,
                             discount = dto.unitDiscount.replace(",","").toDoubleOrNull() ?: 0.0,
                             image = ""
                         )
                     } ?: emptyList()
                }

                val text = if (order.paymentMethodText.isNotEmpty()) {
                    order.paymentMethodText
                } else {
                    state.value.paymentMethods.find { it.id == order.paymentMethod }?.name ?: order.paymentMethod
                }
                
                order.copy(paymentMethodText = text, items = itemsList)
            }
            
            val exportResult = pdfExporter.exportTransactionPdf(
                businessName = businessName,
                staffName = staffName,
                paymentMethod = paymentMethod,
                dateRange = dateRange,
                pageStr = pageStr,
                transactions = enrichedTransactions
            )
            
            hideAllDialogs()
            if (exportResult.openTarget != null) {
                showSnackBar(
                    message = exportResult.message,
                    actionLabel = "Open",
                    onActionPerformed = {
                        val openError = pdfExporter.openPdf(exportResult.openTarget)
                        if (openError != null) {
                            showSnackBar(openError)
                        }
                    }
                )
            } else {
                showSnackBar(exportResult.message)
            }
        }
    }
}
