package com.sundroid.kobokobo.heldOrder.data.network

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.HeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest

interface RemoteHeldOrderDataSource {



    suspend fun getHeldOrders(
        accessToken: String, paginationRequest: PaginationRequest
    ): Result<HeldOrderDto, DataError.Remote>

    suspend fun getSingleHeldOrders(
        accessToken: String,
        voidOrderRequest: VoidOrderRequest
    ): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun holdOrder(
        voidOrderRequest: VoidOrderRequest
    ): Result<ResponseDto, DataError.Remote>

    suspend fun voidOrder(
        accessToken: String,
        voidOrderRequest: VoidOrderRequest
    ): Result<ResponseDto, DataError.Remote>

}