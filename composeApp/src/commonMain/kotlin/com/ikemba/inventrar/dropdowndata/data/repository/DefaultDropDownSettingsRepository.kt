package com.ikemba.inventrar.dropdowndata.data.repository

import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.business.network.RemoteBusinessDataSource
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dropdowndata.data.domain.DropDownSettingsRepository
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.CityResponse
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.CountryResponse
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationTypeResponse
import com.ikemba.inventrar.dropdowndata.data.dto.State
import com.ikemba.inventrar.dropdowndata.data.dto.StateResponse
import com.ikemba.inventrar.dropdowndata.network.RemoteDropDownSettingsDataSource
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

class DefaultDropDownSettingsRepository(
    private val remoteBusinessDataSource: RemoteDropDownSettingsDataSource,
): DropDownSettingsRepository {
    override suspend fun getCountry(request: Country): Result<CountryResponse, DataError.Remote> {
        return remoteBusinessDataSource.getCountry(request)
    }

    override suspend fun getCity(request: City): Result<CityResponse, DataError.Remote> {
        return remoteBusinessDataSource.getCity(request)
    }

    override suspend fun getState(request: State): Result<StateResponse, DataError.Remote> {
     return remoteBusinessDataSource.getState(request)
    }

    override suspend fun getOrganizationType(request: OrganizationType): Result<OrganizationTypeResponse, DataError.Remote> {
        return remoteBusinessDataSource.getOrganizationType(request)
    }

}

