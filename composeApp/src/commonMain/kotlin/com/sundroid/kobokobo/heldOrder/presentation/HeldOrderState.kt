package com.sundroid.kobokobo.heldOrder.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistory

data class HeldOrderState (
    val heldOrders: TransactionHistory = TransactionHistory(),
    val heldOrdersBackUp: TransactionHistory = TransactionHistory(),
    val showProgressDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showConfirmVoidOrder: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var voidOrderReference : String = "",
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val totalOrders: Int = 1,
    val totalPages:  Int = 1,
    val limit:  Int = Util.DEFAULT_LIMIT,
    val page:  Int = 1
    )