package com.sundroid.kobokobo.dashboard.presentation

import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material3.SnackbarHostState
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.cart.domain.CartItem
import com.sundroid.kobokobo.cart.domain.PostSalesModel
import com.sundroid.kobokobo.cart.presentation.ReceiptModel
import com.sundroid.kobokobo.dashboard.domain.Category
import com.sundroid.kobokobo.dashboard.presentation.components.MenuItem
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.user.domain.User

data class DashboardState (
    val userName: String ="",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    var isVisible  : Boolean = false,
    val users: List<User> = emptyList(),
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val visibleProducts: List<Product> = emptyList(),
    var showConfirmLogout: Boolean = false,
    var selectedMenuIndex : MutableTransitionState<Int> = MutableTransitionState(0),
    val cartItems: MutableList<CartItem> = mutableListOf(),
    var paymentMethod: String = "",
    var paymentMethodText: String = "",
    val proceedWithoutPayment: Boolean = false,
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
        MenuItem("Point of Sale", imageUrl = ""),
        MenuItem("Transaction History", imageUrl = "", ),
        MenuItem("Saved Sales", imageUrl = "")
    ),
    val showCartSheet: Boolean =false,
    val heldCarts: List<HeldCartEntity> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList()
)
