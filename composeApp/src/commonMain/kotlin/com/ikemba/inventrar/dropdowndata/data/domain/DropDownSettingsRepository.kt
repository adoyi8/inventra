package com.ikemba.inventrar.dropdowndata.data.domain

import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.CityResponse
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.CountryResponse
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationTypeResponse
import com.ikemba.inventrar.dropdowndata.data.dto.State
import com.ikemba.inventrar.dropdowndata.data.dto.StateResponse
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

interface DropDownSettingsRepository {


    suspend fun getCountry(request: Country): com.ikemba.inventrar.core.domain.Result<CountryResponse, DataError.Remote>
    suspend fun getCity(request: City): com.ikemba.inventrar.core.domain.Result<CityResponse, DataError.Remote>
    suspend fun getState(request: State): com.ikemba.inventrar.core.domain.Result<StateResponse, DataError.Remote>
    suspend fun getOrganizationType(request: OrganizationType): com.ikemba.inventrar.core.domain.Result<OrganizationTypeResponse, DataError.Remote>
}