package com.ikemba.inventrar.heldOrder.data.repository

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.domain.HeldOrderRepository
import com.ikemba.inventrar.heldOrder.data.dto.HeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.heldOrder.data.network.RemoteHeldOrderDataSource
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

class DefaultHeldOrderRepository(
    private val remoteHeldOrderDataSource: RemoteHeldOrderDataSource,
): HeldOrderRepository {
    override suspend fun getSingleHeldOrder(
        accessToken: String,
        request: VoidOrderRequest
    ): Result<SingleHeldOrderDto, DataError.Remote> {
        return remoteHeldOrderDataSource.getSingleHeldOrders(accessToken,request)
    }


    override suspend fun getHeldOrders(accessToken: String, paginationRequest: PaginationRequest): Result<HeldOrderDto, DataError.Remote> {
        return remoteHeldOrderDataSource.getHeldOrders(accessToken, paginationRequest)
    }

    override suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote> {
        return remoteHeldOrderDataSource.voidOrder(accessToken,voidOrderRequest)
    }
}

