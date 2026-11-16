package com.sundroid.kobokobo.cart.data.network

import com.sundroid.kobokobo.cart.data.dto.PostSalesRequest
import com.sundroid.kobokobo.cart.data.dto.ReceiptResponseDto
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest

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