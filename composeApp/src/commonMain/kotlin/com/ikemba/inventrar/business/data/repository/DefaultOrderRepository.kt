package com.ikemba.inventrar.business.data.repository

import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.business.network.RemoteBusinessDataSource
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

class DefaultBusinessRepository(
    private val remoteBusinessDataSource: RemoteBusinessDataSource,
): BusinessRepository {
    override suspend fun getSingleHeldOrder(
        accessToken: String,
        request: VoidOrderRequest
    ): Result<SingleHeldOrderDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


    override suspend fun getBusinesses(accessToken: String, paginationRequest: SearchOrganizationRequest): Result<OrganizationResponse, DataError.Remote> {
        return remoteBusinessDataSource.getBusiness(accessToken, paginationRequest)
    }

    override suspend fun createBusiness(accessToken: String, createBusinessRequest: CreateBusinessRequest): Result<ResponseDto, DataError.Remote> {
       return remoteBusinessDataSource.createBusiness(accessToken, createBusinessRequest)
    }

    override suspend fun voidOrder(
        accessToken: String,
        voidOrderRequest: VoidOrderRequest
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


}

