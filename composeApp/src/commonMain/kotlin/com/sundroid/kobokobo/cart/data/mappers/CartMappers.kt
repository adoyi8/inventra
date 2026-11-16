package com.sundroid.kobokobo.cart.data.mappers


import com.sundroid.kobokobo.cart.data.database.CartEntity
import com.sundroid.kobokobo.cart.data.database.PostSalesItemEntity
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestEntity
import com.sundroid.kobokobo.cart.data.dto.PostSalesItem
import com.sundroid.kobokobo.cart.data.dto.PostSalesRequest
import com.sundroid.kobokobo.cart.domain.Cart
import com.sundroid.kobokobo.cart.domain.PostSalesItemModel
import com.sundroid.kobokobo.cart.domain.PostSalesModel
import kotlinx.serialization.json.Json


fun Cart.toCartEntity(): CartEntity {
    return CartEntity(
        cartId = cartId,
        //cartItems = items,
        paymentMethod = paymentMethod,
        paymentMethodText = paymentMethodText,
        reference = reference
    )
}

fun Cart.toPostSalesRequest(): PostSalesRequest {
    println("ndiaye 1")
    val result = PostSalesRequest(
        businessId = this.businessId,
        userId = this.userId,
        userName = this.userName,
        type = "sales"
    )
    println("ndiaye 2")
    result.payment_method = this.paymentMethod
    println("ndiaye 3")
    result.reference = reference
    println("ndiaye 4")
    result.total = this.getTotal()
    println("ndiaye 5")
    result.total_vat = this.getTotalVat()
    println("ndiaye 6")
    result.grandTotal = this.getGrandTotal()
    println("ndiaye 7")
    result.total_profit = this.getTotalProfit()
    result.payment_method_text = this.paymentMethodText
    result.is_paid = this.isPaid
    result.payment_processed_by = this.paymentProcessedBy
    result.cart = this.items.map {
        println("Shogun "+ it.costPrice)
        PostSalesItem(
            product_id = it.itemId!!,
            product_name = it.itemName,
            qty = it.quantity.toString(),
            subtotal = it.getSubTotal(),
            vat_rate = it.returnVatRate(),
            vat_amount = it.getVatAmount(),
            discount = it.discount.toString(),
            costPrice = it.costPrice, // <--- NEW: Add cost price for the item
            profit = it.getProfitPerItem()
        )
    }
    return result

}

fun PostSalesRequest.toPostSalesRequestEntity(): PostSalesRequestEntity {
    val result= PostSalesRequestEntity(
        reference = reference!!,
        payment_method = payment_method,
        payment_method_text = payment_method_text,
        is_paid = is_paid,
        payment_processed_by = payment_processed_by,
        total = total,
        total_vat = total_vat,
        grandTotal = grandTotal,
        totalProfit = total_profit, // <--- NEW
        syncedStatus = false,
        businessId = businessId,
        userId = userId,
        userName = userName,
        type = type
    )
    result.cart = Json.encodeToString(
        this.cart!!.map { item ->
            PostSalesItemEntity(
                product_id = item.product_id, // Ensure this is String
                product_name = item.product_name,
                qty = item.qty,
                subtotal = item.subtotal,
                vat_rate = item.vat_rate,
                vat_amount = item.vat_amount,
                discount = item.discount,
                costPrice = item.costPrice, // <--- NEW
                profit = item.profit // <--- NEW
            )
        }
    )
    return result;
}

fun PostSalesRequestEntity.toPostSalesModel(): PostSalesModel {
    val result= PostSalesModel(
        reference = reference!!,
        payment_method = payment_method,
        total = total,
        total_vat = total_vat,
        grandTotal = grandTotal,
        totalProfit = totalProfit, // <--- NEW
        businessId = businessId,
        userId = userId,
        userName = userName,
        type = type,
        payment_method_text = payment_method_text,
        is_paid = is_paid,
        payment_processed_by = payment_processed_by


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
        total_profit = totalProfit, // <--- NEW
        businessId = businessId,
        userId = this.userId,
        userName = this.userName,
        type = type,
        payment_method_text = payment_method_text,
        is_paid = is_paid,
        payment_processed_by = payment_processed_by
    )
    result.cart = this.cart!!.map { item ->
        PostSalesItem(
            product_id = item.product_id,
            product_name = item.product_name,
            qty = item.qty,
            subtotal = item.subtotal,
            vat_rate = item.vat_rate,
            vat_amount = item.vat_amount,
            discount = item.discount,
            costPrice = item.costPrice, // <--- NEW
            profit = item.profit // <--- NEW
        )
    }
    return result;
}
