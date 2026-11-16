package com.sundroid.kobokobo.expense.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.expense.domain.ExpenseHistory

data class ExpenseState(
    val expenseHistory: ExpenseHistory = ExpenseHistory(),
    val showProgressDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val totalOrders: Int = 1,
    val totalPages: Int = 1,
    val limit: Int = Util.DEFAULT_LIMIT,
    val page: Int = 1,
    val staffList: List<Staff> = emptyList(),
    val expenseCategories: List<String> = emptyList() // <-- For filter dropdown
)