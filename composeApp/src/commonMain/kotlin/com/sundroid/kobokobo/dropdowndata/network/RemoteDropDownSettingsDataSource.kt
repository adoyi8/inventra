package com.sundroid.kobokobo.dropdowndata.network

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.CityResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.CountryResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationTypeResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.StateResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.State

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