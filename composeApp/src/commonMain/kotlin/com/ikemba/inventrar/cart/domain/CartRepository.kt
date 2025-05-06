package com.ikemba.inventrar.cart.domain

import com.ikemba.inventrar.cart.data.database.CartEntity
import com.ikemba.inventrar.cart.data.database.PostSalesRequestEntity
import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import kotlinx.coroutines.flow.Flow

interface CartRepository {




    suspend fun holdOrder(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto?, DataError>
    suspend fun postCartSales(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto?, DataError>
    suspend fun getOrderReceipt(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ReceiptResponseDto?, DataError>
    suspend fun saveCart(cart: CartEntity): EmptyResult<DataError.Local>
    suspend fun deleteCart(cartId: String): EmptyResult<DataError.Local>
    suspend fun updateCart(cart: CartEntity): EmptyResult<DataError.Local>
    suspend fun saveSales(salesRequestEntity: PostSalesRequestEntity): EmptyResult<DataError.Local>
    suspend fun deleteSales(reference: String): EmptyResult<DataError.Local>
    fun getAllCarts(): Flow<List<CartEntity>>
    fun getAllSales(): Flow<List<PostSalesRequestEntity>>
}