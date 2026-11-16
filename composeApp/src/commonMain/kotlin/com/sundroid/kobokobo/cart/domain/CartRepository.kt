package com.sundroid.kobokobo.cart.domain

import com.sundroid.kobokobo.cart.data.database.CartEntity
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestEntity
import com.sundroid.kobokobo.cart.data.dto.PostSalesRequest
import com.sundroid.kobokobo.cart.data.dto.ReceiptResponseDto
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import kotlinx.coroutines.flow.Flow

interface CartRepository {




    suspend fun holdOrder(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto?, DataError>
    suspend fun postCartSales(postSalesRequest: PostSalesRequest): Result<ResponseDto?, DataError>
    suspend fun deleteCartSales(voidOrderRequest: VoidOrderRequest): Result<ResponseDto?, DataError>
    suspend fun getOrderReceipt(voidOrderRequest: VoidOrderRequest): Result<ReceiptResponseDto?, DataError>
    suspend fun markSalePaid(businessId: String, reference: String, paymentMethod: String, paymentMethodText: String, processedBy: String): Result<ResponseDto?, DataError>
    suspend fun saveCart(cart: CartEntity): EmptyResult<DataError.Local>
    suspend fun deleteCart(cartId: String): EmptyResult<DataError.Local>
    suspend fun updateCart(cart: CartEntity): EmptyResult<DataError.Local>
    suspend fun saveSales(salesRequestEntity: PostSalesRequestEntity): EmptyResult<DataError.Local>
    suspend fun deleteSales(reference: String): EmptyResult<DataError.Local>
    fun getAllCarts(): Flow<List<CartEntity>>
    fun getAllSales(): Flow<List<PostSalesRequestEntity>>
    suspend fun saveHeldCart(heldCart: HeldCartEntity): EmptyResult<DataError.Local>
    suspend fun deleteHeldCart(heldCart: HeldCartEntity): EmptyResult<DataError.Local>
    fun getAllHeldCarts(): Flow<List<HeldCartEntity>>
}
