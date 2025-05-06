package com.ikemba.inventrar.cart.data.network

import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

interface RemoteCartDataSource {
    suspend fun postSales(
        accessToken: String, postSalesRequest: PostSalesRequest
    ): Result<ResponseDto, DataError.Remote>

    suspend fun holdOrder(
        accessToken: String, postSalesRequest: PostSalesRequest
    ): Result<ResponseDto, DataError.Remote>
    suspend fun getOrderReceipt(
        accessToken: String, getReceiptRequest: VoidOrderRequest
    ): Result<ReceiptResponseDto, DataError.Remote>
}