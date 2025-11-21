package com.ikemba.inventrar.transactionHistory.presentation


import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.cart.data.mappers.toReceipt
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.transactionHistory.data.mappers.toTransactionHistory
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistory
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistoryRepository
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

    fun toggleShowReceipt(b: Boolean){
        hideAllDialogs()
        _state.update {
            it.copy(
                showReceipt = MutableTransitionState(b)
            )

        }
    }
    fun showSnackBar(message: String){
        viewModelScope.launch {
            state.value.snackBarHostState.showSnackbar(message)
        }
    }
}