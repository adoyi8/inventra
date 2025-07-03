package com.ikemba.inventrar.dropdowndata.network


import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.utils.Util.SETTINGS_SERVICE_URL
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.CityResponse
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.CountryResponse
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationTypeResponse
import com.ikemba.inventrar.dropdowndata.data.dto.State
import com.ikemba.inventrar.dropdowndata.data.dto.StateResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType





class KtorRemoteDropDownSettingsDataSource(
    private val httpClient: HttpClient
): RemoteDropDownSettingsDataSource {



    override suspend fun getCountry(

        request: Country
    ): Result<CountryResponse, DataError.Remote> {
        return safeCall<CountryResponse> {
            httpClient.post(
                urlString = "${SETTINGS_SERVICE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)
                setBody(request)

            }
        }
    }

    override suspend fun getState(
        request: State
    ): Result<StateResponse, DataError.Remote> {
        return safeCall<StateResponse> {
            httpClient.post(
                urlString = "${SETTINGS_SERVICE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)

                setBody(request)

            }
        }
    }

    override suspend fun getCity(

        request: City
    ): Result<CityResponse, DataError.Remote> {
        return safeCall<CityResponse> {
            httpClient.post(
                urlString = "${SETTINGS_SERVICE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)

                setBody(request)

            }
        }
    }

    override suspend fun getOrganizationType(

        request: OrganizationType
    ): Result<OrganizationTypeResponse, DataError.Remote> {
        return safeCall<OrganizationTypeResponse> {
            httpClient.post(
                urlString = "${SETTINGS_SERVICE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)
                setBody(request)

            }
        }
    }
}