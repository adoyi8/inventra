package com.sundroid.kobokobo.dropdowndata.data.repository

import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.dto.CreateBusinessRequest
import com.sundroid.kobokobo.business.data.dto.OrganizationResponse
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.business.network.RemoteBusinessDataSource
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dropdowndata.data.domain.DropDownSettingsRepository
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.CityResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.CountryResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationTypeResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.State
import com.sundroid.kobokobo.dropdowndata.data.dto.StateResponse
import com.sundroid.kobokobo.dropdowndata.network.RemoteDropDownSettingsDataSource
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest

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

