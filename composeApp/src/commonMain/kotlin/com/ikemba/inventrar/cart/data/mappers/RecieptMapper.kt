package com.ikemba.inventrar.cart.data.mappers

import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.cart.domain.CartItem
import com.ikemba.inventrar.cart.presentation.ReceiptModel

fun ReceiptResponseDto.toReceipt(): ReceiptModel {
    val receiptModel = ReceiptModel()
    this.data?.let {
        receiptModel.cashier = this.data.summary.first().salesPerson
        receiptModel.address = this.data.summary.first().businessAddress
        receiptModel.time = this.data.summary.first().actionTime
        receiptModel.date = this.data.summary.first().created
        receiptModel.subTotal = this.data.summary.first().subTotal.toDouble()
        receiptModel.grandTotal = this.data.summary.first().grandTotal.toDouble()
        receiptModel.taxTotal = this.data.summary.first().taxTotal.toDouble()
        receiptModel.logo = this.data.summary.first().logo
        receiptModel.reference = this.data.summary.first().orderNo
        receiptModel.discount = this.data.summary.first().discountTotal.toDouble()

        this.data.items.forEach {
            val cartItem = CartItem(itemId = it.productId, itemName= it.productName , quantity = it.quantity.toInt(), price = it.unitPrice.toDouble(), vatRate = it.unitVat.toDouble())
            receiptModel.cartItems.add(cartItem)
        }
    }

    return receiptModel
}