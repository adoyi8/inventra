package com.ikemba.inventrar.cart.presentation

import com.ikemba.inventrar.cart.domain.CartItem

data class ReceiptModel(

    var address: String = "",
    var reference: String = "",
    var date: String = "",
    var time: String = "",
    var cashier : String ="",
    var subTotal: Double = 0.0,
    var discount: Double = 0.0,
    var vat: Double = 0.0,
    var total: Double = 0.0,
    var items: MutableList<String> = mutableListOf(),
    var totalItems: Int = 0,
    var taxTotal: Double = 0.0,
    var grandTotal: Double =0.0,
    var logo: String? = null,
    var cartItems: MutableList<CartItem> = mutableListOf(),
    var businessName: String = ""
)