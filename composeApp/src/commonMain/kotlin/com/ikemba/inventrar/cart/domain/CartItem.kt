package com.ikemba.inventrar.cart.domain

data class CartItem (
    val itemId: String?,
    val itemName: String?,
    var quantity: Int,
    var price: Double =0.0,
    val vatRate: Double = 0.0,
    val discount: Double? =0.0,
    val image: String? = ""
){
    fun addQuantity(value: Int){
        quantity += 1
    }
    fun removeQuantity(value: Int){
        quantity -= 1
    }
    fun getSubTotal():Double{
        return if(discount!=null){
            (quantity*price) - getSubDiscount()
        }
        else{
            (quantity*price)
        }

    }
    fun getVatAmount(): Double{
        return if(vatRate != null){
            getSubTotal() * vatRate * 0.01
        }
        else{
            0.0
        }
    }
    fun returnVatRate(): Double{

        return vatRate ?: 0.0
    }
    fun getSubDiscount(): Double{
        return discount?.times(quantity) ?: 0.0;
    }
}