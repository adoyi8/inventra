package com.ikemba.inventrar.cart.domain

data class Cart(
    val items: MutableList<CartItem>,
    val cartId: String = "",
    var paymentMethod: String? = null,
    var reference: String = "",
    val totalPrice: Double? = null,
    val totalQuantity: Int? = null,
    val subtotal: Double? = null,
    val shippingCost: Double? = null,
    val tax: Double?    = null,

    ){

    fun getTotal(): Double{
        return items.sumOf { it.getSubTotal() }
    }

    fun getTotalVat(): Double{
        return items.sumOf { it.getVatAmount() }
    }
    fun getGrandTotal(): Double{
        return getTotal()+ getTotalVat()
    }
    fun getTotalDiscount(){

    }
}


