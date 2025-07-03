package com.ikemba.inventrar.dropdowndata.network

import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.CityResponse
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.CountryResponse
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationTypeResponse
import com.ikemba.inventrar.dropdowndata.data.dto.StateResponse
import com.ikemba.inventrar.dropdowndata.data.dto.State

interface RemoteDropDownSettingsDataSource {
    suspend fun getCountry(request: Country
    ): Result<CountryResponse, DataError.Remote>
    suspend fun getState(
        request: State
    ): Result<StateResponse, DataError.Remote>
    suspend fun getCity(
        request: City
    ): Result<CityResponse, DataError.Remote>
    suspend fun getOrganizationType(request: OrganizationType
    ): Result<OrganizationTypeResponse, DataError.Remote>
}