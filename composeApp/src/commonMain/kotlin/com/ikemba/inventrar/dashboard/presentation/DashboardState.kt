package com.ikemba.inventrar.dashboard.presentation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.ikemba.inventrar.cart.domain.CartItem
import com.ikemba.inventrar.cart.domain.PostSalesModel
import com.ikemba.inventrar.cart.presentation.ReceiptModel
import com.ikemba.inventrar.dashboard.domain.Category
import com.ikemba.inventrar.dashboard.domain.Item
import com.ikemba.inventrar.dashboard.presentation.components.MenuItem
import com.ikemba.inventrar.user.domain.User

data class DashboardState (
    val userName: String ="",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    var isVisible  : Boolean = false,
    val users: List<User> = emptyList(),
    val categories: List<Category> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val visibleItems: List<Item> = emptyList(),
    var showConfirmLogout: Boolean = false,
    var showChangePasswordDialog: Boolean = false,
    var selectedMenuIndex : MutableTransitionState<Int> = MutableTransitionState(0),
    val cartItems: MutableList<CartItem> = mutableListOf(),
    var paymentMethod: String = "",
    var showConfirmTransaction: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showPostSalesDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showReceipt: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var showPaymentMethodNotAvailableDialog: MutableTransitionState<Boolean> = MutableTransitionState(false),
    var receiptModel: ReceiptModel = ReceiptModel(),
    val savedSales: List<PostSalesModel> = emptyList(),
    val showSavingSalesDialog : MutableTransitionState<Boolean> = MutableTransitionState(false),
    val disableShowSavingSalesDialog : Boolean = false,
    val snackBarHostState: SnackbarHostState = SnackbarHostState(),
    val snackBarMessage: String = "",
    val reference: String = "",
    val menu: List<MenuItem> = listOf<MenuItem>(
        MenuItem("Point of Sale", imageUrl = ""), MenuItem("Paused Orders", imageUrl = ""),
        MenuItem("Transaction History", imageUrl = "", ),
        MenuItem("Saved Sales", imageUrl = ""),
        MenuItem("Settings", imageUrl = "",)
    )
    )