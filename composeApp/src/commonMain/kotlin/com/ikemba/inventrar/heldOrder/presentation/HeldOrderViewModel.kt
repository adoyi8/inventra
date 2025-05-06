package com.ikemba.inventrar.heldOrder.presentation


import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.heldOrder.data.domain.HeldOrderRepository
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.heldOrder.data.mappers.toTransactionHistory
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HeldOrderViewModel(
    private val heldOrderRepository: HeldOrderRepository,
): ViewModel(){
    private val _state = MutableStateFlow(HeldOrderState())



    val state = _state.asStateFlow()
        .onStart {
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun getHeldOrders(paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            heldOrderRepository.getHeldOrders(Util.accessToken, paginationRequest)
                .onSuccess { heldOrdersResponse ->
                    if(heldOrdersResponse.responseCode == 0){
                        val orders = heldOrdersResponse.toTransactionHistory()
                        if(heldOrdersResponse.data == null){
                            _state.update {
                                it.copy(heldOrders = TransactionHistory(), heldOrdersBackUp = TransactionHistory(), showProgressDialog = MutableTransitionState(false), page = orders.page, limit = orders.limit, totalPages = orders.totalPages, totalOrders = orders.totalOrders)
                            }
                        }
                        else {
                            _state.update {
                                it.copy(
                                    heldOrders = orders,
                                    heldOrdersBackUp = orders,
                                    showProgressDialog = MutableTransitionState(false),
                                    page = orders.page,
                                    limit = orders.limit,
                                    totalPages = orders.totalPages,
                                    totalOrders = orders.totalOrders
                                )
                            }
                        }
                    }
                    else{
                        _state.update {
                            it.copy(showProgressDialog = MutableTransitionState(false), heldOrders = TransactionHistory(), heldOrdersBackUp = TransactionHistory())
                        }
                        showSnackBar(heldOrdersResponse.responseMessage.toString())
                    }
                }
                .onError { error->
                    _state.update {
                        it.copy(showProgressDialog = MutableTransitionState(false))
                    }
                    if(error.equals(DataError.Remote.EXPIRED_TOKEN)){
                        NavigationViewModel.navController?.navigate(Route.Login)
                    }
                    showSnackBar(error.toString())

                }
        }
    }
    fun voidOrder(paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            hideAllDialogs()
            _state.update {

                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            val voidOrderRequest = VoidOrderRequest(state.value.voidOrderReference)
            heldOrderRepository.voidOrder(Util.accessToken, voidOrderRequest)
                .onSuccess { heldOrdersResponse ->
                    if(heldOrdersResponse.responseCode == 0){
                       getHeldOrders(paginationRequest)
                    }
                    else{
                        _state.update {
                            it.copy(showProgressDialog = MutableTransitionState(false))
                        }
                        showSnackBar(heldOrdersResponse.responseMessage.toString())
                    }
                }
                .onError { error->
                    _state.update {
                        it.copy(showProgressDialog = MutableTransitionState(false))
                    }
                    showSnackBar(error.toString())
                }
        }
    }

    fun getVoidedOrder(reference:String, dashboardViewModel: DashboardViewModel) {
        viewModelScope.launch {
            _state.update {
                it.copy(showProgressDialog = MutableTransitionState(true))
            }
            val voidOrderRequest = VoidOrderRequest(reference)
            heldOrderRepository.getSingleHeldOrder(Util.accessToken, voidOrderRequest)
                .onSuccess { heldOrdersResponse ->
                 if(heldOrdersResponse.responseCode ==0){
                     dashboardViewModel.proceedOrders(heldOrdersResponse, reference)
                     _state.update {
                         it.copy(showProgressDialog = MutableTransitionState(false))
                     }
                 }
                    else{
                        _state.update {
                            it.copy(showProgressDialog = MutableTransitionState(false))
                        }
                     showSnackBar(heldOrdersResponse.responseMessage.toString())
                 }
                }
                .onError { error->
                    _state.update {
                        it.copy(showProgressDialog = MutableTransitionState(false))
                    }
                    showSnackBar(error.toString())
                }
        }
    }

    fun setVoidOrderReferenceAndShowConfirmVoidDialog(orderReference: String){
        _state.update {
            it.copy(voidOrderReference = orderReference, showConfirmVoidOrder = MutableTransitionState(true))
        }
    }

    fun hideAllDialogs() {
        _state.update {
            it.copy(showConfirmVoidOrder = MutableTransitionState(false), showProgressDialog = MutableTransitionState(false))
        }
    }
    fun searchHeldOrder(query: String, paginationRequest: MutableState<PaginationRequest>) {
        paginationRequest.value.keyword = query
        paginationRequest.value.filter_by = null
        paginationRequest.value.start_date = null
        paginationRequest.value.end_date = null
        getHeldOrders(paginationRequest.value)
    }
    fun selectFilter(filter: String, paginationRequest: MutableState<PaginationRequest>) {
        if(filter.equals("select::")){
            paginationRequest.value.filter_by = null
        }
        else{
            paginationRequest.value.filter_by = filter
        }

        paginationRequest.value.start_date = null
        paginationRequest.value.end_date = null
        getHeldOrders(paginationRequest.value)
    }
    fun searchByDateRange(startDate: String, endDate: String, paginationRequest: MutableState<PaginationRequest>) {
        paginationRequest.value.filter_by = null
        paginationRequest.value.start_date = startDate
        paginationRequest.value.end_date = endDate
        getHeldOrders(paginationRequest.value)
    }
    fun showSnackBar(message: String){
        viewModelScope.launch {
            state.value.snackBarHostState.showSnackbar(message)
        }
    }
}