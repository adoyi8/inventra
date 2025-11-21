package com.ikemba.inventrar.expense.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.expense.domain.CreateExpenseUseCase
import com.ikemba.inventrar.expense.domain.ExpenseDto
import com.ikemba.inventrar.expense.domain.GetExpenseCategoriesUseCase
import com.ikemba.inventrar.expense.domain.GetExpensesUseCase
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val businessRepository: BusinessRepository, // To get staff
    private val businessId: String
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    init {
        fetchStaffList()
        fetchExpenseCategories()
    }

    private fun fetchStaffList() {
        viewModelScope.launch {
            businessRepository.getStaffForBusiness(businessId)
                .onSuccess { list ->
                    _state.update { it.copy(staffList = list) }
                }
        }
    }

    private fun fetchExpenseCategories() {
        viewModelScope.launch {
            getExpenseCategoriesUseCase(businessId)
                .onSuccess { list ->
                    _state.update { it.copy(expenseCategories = list) }
                }
        }
    }

    fun getExpenses(paginationRequest: PaginationRequest) {
        viewModelScope.launch {
            _state.update { it.copy(showProgressDialog = MutableTransitionState(true)) }
            getExpensesUseCase(businessId, paginationRequest)
                .onSuccess { history ->
                    _state.update {
                        it.copy(
                            expenseHistory = history,
                            showProgressDialog = MutableTransitionState(false),
                            page = history.page,
                            limit = history.limit,
                            totalPages = history.totalPages,
                            totalOrders = history.totalOrders
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(showProgressDialog = MutableTransitionState(false)) }
                    showSnackBar(error.toString())
                }
        }
    }

    fun addExpense(
        expenseDto: ExpenseDto,
        currentRequest: PaginationRequest
    ) {
        viewModelScope.launch {
            _state.update { it.copy(showProgressDialog = MutableTransitionState(true)) }
            createExpenseUseCase(businessId, expenseDto)
                .onSuccess {
                    _state.update { it.copy(showProgressDialog = MutableTransitionState(false)) }
                    showSnackBar("Expense recorded")
                    getExpenses(currentRequest) // Refresh the list
                    fetchExpenseCategories() // Refresh categories in case a new one was added
                }
                .onError { error ->
                    _state.update { it.copy(showProgressDialog = MutableTransitionState(false)) }
                    showSnackBar(error.toString())
                }
        }
    }

    fun applyFilters(newRequest: PaginationRequest) {
        newRequest.page = 1
        newRequest.keyword = null
        getExpenses(newRequest)
    }

    fun searchExpenses(query: String, paginationRequest: MutableState<PaginationRequest>) {
        val newSearchRequest = PaginationRequest(
            keyword = query.takeIf { it.isNotBlank() },
            page = 1,
            limit = paginationRequest.value.limit
        )
        paginationRequest.value = newSearchRequest
        getExpenses(newSearchRequest)
    }

    fun showSnackBar(message: String){
        viewModelScope.launch {
            state.value.snackBarHostState.showSnackbar(message)
        }
    }
}