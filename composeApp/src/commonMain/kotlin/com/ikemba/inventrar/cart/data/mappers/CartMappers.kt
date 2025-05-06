package com.ikemba.inventrar.cart.data.mappers


import com.ikemba.inventrar.cart.data.database.CartEntity
import com.ikemba.inventrar.cart.data.database.PostSalesItemEntity
import com.ikemba.inventrar.cart.data.database.PostSalesRequestEntity
import com.ikemba.inventrar.cart.data.dto.PostSalesItem
import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.domain.Cart
import com.ikemba.inventrar.cart.domain.PostSalesItemModel
import com.ikemba.inventrar.cart.domain.PostSalesModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Cart.toCartEntity(): CartEntity {
    return CartEntity(
        cartId = cartId,
        //cartItems = items,
        paymentMethod = paymentMethod,
        reference = reference
    )
}

fun Cart.toPostSalesRequest(): PostSalesRequest {
    val result = PostSalesRequest()
    result.payment_method = this.paymentMethod
    result.reference = reference
    result.total = this.getTotal()
    result.total_vat = this.getTotalVat()
    result.grandTotal = this.getGrandTotal()
    result.cart = this.items.map {
        PostSalesItem(product_id = it.itemId!!.toInt(), product_name = it.itemName, qty = it.quantity.toString(),subtotal = it.getSubTotal(), vat_rate = it.returnVatRate(), vat_amount = it.getVatAmount())
    }
    return result

}

fun PostSalesRequest.toPostSalesRequestEntity(): PostSalesRequestEntity {
    val result= PostSalesRequestEntity(
        reference = reference!!,
        payment_method = payment_method,
        total = total,
        total_vat = total_vat,
        grandTotal = grandTotal,
        syncedStatus = false
    )
    result.cart = Json.encodeToString(
        this.cart!!.map {
        PostSalesItemEntity(
            product_id = it.product_id.toInt(),
            product_name = it.product_name,
            qty = it.qty,
            subtotal = it.subtotal,
            vat_rate = it.vat_rate,
            vat_amount = it.vat_amount
        )
    })
    return result;
}
fun PostSalesRequestEntity.toPostSalesModel(): PostSalesModel {
    val result= PostSalesModel(
        reference = reference!!,
        payment_method = payment_method,
        total = total,
        total_vat = total_vat,
        grandTotal = grandTotal,
    )
    result.cart = Json.decodeFromString<List<PostSalesItemModel>>(this.cart ?: "[]")
    return result;
}

fun PostSalesModel.toPostSalesRequest(): PostSalesRequest {
    val result= PostSalesRequest(
        reference = reference!!,
        payment_method = payment_method,
        total = total,
        total_vat = total_vat,
        grandTotal = grandTotal,
    )
    result.cart = this.cart!!.map {
        PostSalesItem(
            product_id = it.product_id!!.toInt(),
            product_name = it.product_name,
            qty = it.qty,
            subtotal = it.subtotal,
            vat_rate = it.vat_rate,
            vat_amount = it.vat_amount
        )
    }
    return result;
}