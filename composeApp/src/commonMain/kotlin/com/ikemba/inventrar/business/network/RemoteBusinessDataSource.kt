package com.ikemba.inventrar.business.network

import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
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

    suspend fun updateBusiness(
        accessToken: String, request: CreateBusinessRequest
    ): Result<ResponseDto, DataError.Remote>

    suspend fun createBusiness(
        accessToken: String, request: CreateBusinessRequest
    ): Result<ResponseDto, DataError.Remote>

    suspend fun deleteBusiness(
        accessToken: String,
        request: String
    ): Result<ResponseDto, DataError.Remote>

}