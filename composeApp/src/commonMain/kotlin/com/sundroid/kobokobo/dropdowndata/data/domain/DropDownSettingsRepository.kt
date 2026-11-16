package com.sundroid.kobokobo.dropdowndata.data.domain

import com.sundroid.kobokobo.business.data.dto.CreateBusinessRequest
import com.sundroid.kobokobo.business.data.dto.OrganizationResponse
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.CityResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.CountryResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationTypeResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.State
import com.sundroid.kobokobo.dropdowndata.data.dto.StateResponse
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest

interface DropDownSettingsRepository {


    suspend fun getCountry(request: Country): com.sundroid.kobokobo.core.domain.Result<CountryResponse, DataError.Remote>
    suspend fun getCity(request: City): com.sundroid.kobokobo.core.domain.Result<CityResponse, DataError.Remote>
    suspend fun getState(request: State): com.sundroid.kobokobo.core.domain.Result<StateResponse, DataError.Remote>
    suspend fun getOrganizationType(request: OrganizationType): com.sundroid.kobokobo.core.domain.Result<OrganizationTypeResponse, DataError.Remote>
}