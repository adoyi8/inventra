package com.sundroid.kobokobo.transactionHistory.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.cart.presentation.ReceiptModel
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistory

data class TransactionHistoryState (
    val transactionHistory: TransactionHistory = TransactionHistory(),
    val backUpHistory: TransactionHistory = TransactionHistory(),
    val showProgressDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showReceipt: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var receiptModel: ReceiptModel = ReceiptModel(),
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val totalOrders: Int = 1,
    val totalPages:  Int = 1,
    val limit:  Int = Util.DEFAULT_LIMIT,
    val page:  Int = 1,
    val staffList: List<Staff> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    // Stats for Dialog
    val pageStats: TransactionPageStats? = null,
    val showPageStatsDialog: Boolean = false,
    val isLoadingStats: Boolean = false
)

data class TransactionPageStats(
    val totalSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val itemBreakdown: List<ItemBreakdown> = emptyList()
)

data class ItemBreakdown(
    val name: String,
    val quantity: Double // Quantity can be decimal (e.g. kg)
)