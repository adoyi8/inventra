package com.ikemba.inventrar.business.data.domain

import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

interface BusinessRepository {


    suspend fun getSingleHeldOrder(accessToken: String, request: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun getBusinesses(accessToken: String, searchOrganizationRequest: SearchOrganizationRequest): Result<OrganizationResponse, DataError.Remote>
    suspend fun createBusiness(accessToken: String, createBusinessRequest: CreateBusinessRequest): Result<ResponseDto, DataError.Remote>
    suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote>
}