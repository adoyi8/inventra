package com.ikemba.inventrar.transactionHistory.presentation


import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
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
    private val cartRepository: CartRepository
): ViewModel(){
    private val _state = MutableStateFlow(TransactionHistoryState())



    val state = _state.asStateFlow()
        .onStart {
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun getTransactionHistory(paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            println("called access token "+ Util.accessToken)
            transactionHistoryRepository.getTransactionHistory(Util.accessToken, paginationRequest)
                .onSuccess { transactionHistoryResponse ->
                    println("Sour pie 1")
                    if(transactionHistoryResponse.responseCode == 0){
                        val history = transactionHistoryResponse.toTransactionHistory()
                        _state.update {
                            it.copy(transactionHistory = history, showProgressDialog = MutableTransitionState(false), page = history.page, limit = history.limit, totalPages = history.totalPages, totalOrders = history.totalOrders)
                        }
                    }
                    else{
                        println("Sour pie 3")
                        if(transactionHistoryResponse.responseMessage!!.contains(Util.EXPIRED_TOKEN_MESSAGE, true)) {
                            println("Sour")
                            NavigationViewModel.navController!!.navigate(Route.Login)
                        }
                        println("Sour pie 5")
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
        paginationRequest.value.keyword = query
        getTransactionHistory(paginationRequest.value)
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
                            Util.accessToken,
                            VoidOrderRequest(reference)
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