package com.sundroid.kobokobo.heldOrder.data.repository

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.domain.HeldOrderRepository
import com.sundroid.kobokobo.heldOrder.data.dto.HeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.heldOrder.data.network.RemoteHeldOrderDataSource
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest

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

