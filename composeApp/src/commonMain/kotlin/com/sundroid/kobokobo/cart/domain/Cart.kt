package com.sundroid.kobokobo.cart.domain

data class Cart(
    val items: MutableList<CartItem>,
    val cartId: String = "",
    var paymentMethod: String? = null,
    var paymentMethodText: String? = null,
    var isPaid: Boolean = false,
    var paymentProcessedBy: String? = null,
    var reference: String = "",
    val totalPrice: Double? = null,
    val totalQuantity: Int? = null,
    val subtotal: Double? = null,
    val shippingCost: Double? = null,
    val tax: Double?    = null,
    val businessId: String,
    val userId: String,
    val userName: String

    ){


    fun getTotalDiscount(){

    }
    fun getTotal(): Double = items.sumOf { it.getSubTotal() }
    fun getTotalVat(): Double = items.sumOf { it.getVatAmount() }
    fun getGrandTotal(): Double = getTotal() + getTotalVat()
    fun getTotalProfit(): Double = items.sumOf { it.getProfitPerItem() }
}


