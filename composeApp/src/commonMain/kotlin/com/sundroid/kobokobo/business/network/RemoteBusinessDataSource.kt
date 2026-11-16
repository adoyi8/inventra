package com.sundroid.kobokobo.business.network

import com.sundroid.kobokobo.business.data.dto.CreateBusinessRequest
import com.sundroid.kobokobo.business.data.dto.OrganizationResponse
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest

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