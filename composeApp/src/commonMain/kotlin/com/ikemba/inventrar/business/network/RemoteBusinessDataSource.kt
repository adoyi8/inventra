package com.ikemba.inventrar.business.network

import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

interface RemoteBusinessDataSource {



    suspend fun getBusiness(
        accessToken: String, paginationRequest: SearchOrganizationRequest
    ): Result<OrganizationResponse, DataError.Remote>

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