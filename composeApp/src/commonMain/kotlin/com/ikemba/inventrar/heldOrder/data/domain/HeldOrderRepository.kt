package com.ikemba.inventrar.heldOrder.data.domain

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.HeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

interface HeldOrderRepository {


    suspend fun getSingleHeldOrder(accessToken: String, request: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun getHeldOrders(accessToken: String, paginationRequest: PaginationRequest): Result<HeldOrderDto, DataError.Remote>
    suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote>
    suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote>

}