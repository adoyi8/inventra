package com.ikemba.inventrar.heldOrder.data.network

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.HeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

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