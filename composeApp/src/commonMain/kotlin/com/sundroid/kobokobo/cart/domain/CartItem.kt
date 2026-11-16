package com.sundroid.kobokobo.cart.domain

import kotlinx.serialization.Serializable


@Serializable
data class CartItem (
    val itemId: String?,
    val itemName: String?,
    var quantity: Int,
    var price: Double =0.0,
    val costPrice: Double = 0.0,
    val vatRate: Double = 0.0,
    val discount: Double? =0.0,
    val image: String? = ""
)
{
    fun getProfitPerItem(): Double {
        return getSubTotal() - (quantity * costPrice)
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