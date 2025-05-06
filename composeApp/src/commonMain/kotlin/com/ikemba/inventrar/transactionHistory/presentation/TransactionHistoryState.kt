package com.ikemba.inventrar.transactionHistory.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.ikemba.inventrar.cart.presentation.ReceiptModel
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistory

data class TransactionHistoryState (
    val transactionHistory: TransactionHistory = TransactionHistory(),
    val backUpHistory: TransactionHistory = TransactionHistory(),
    val showProgressDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showReceipt: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var receiptModel: ReceiptModel = ReceiptModel(),
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val totalOrders: Int = 1,
    val totalPages:  Int = 1,
    val limit:  Int = 10,
    val page:  Int = 1
    )