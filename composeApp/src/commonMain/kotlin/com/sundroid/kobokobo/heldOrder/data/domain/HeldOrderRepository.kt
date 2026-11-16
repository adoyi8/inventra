package com.sundroid.kobokobo.heldOrder.data.domain

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.HeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest

interface HeldOrderRepository {


    suspend fun getSingleHeldOrder(accessToken: String, request: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun getHeldOrders(accessToken: String, paginationRequest: PaginationRequest): Result<HeldOrderDto, DataError.Remote>
    suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote>
    suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote>

}