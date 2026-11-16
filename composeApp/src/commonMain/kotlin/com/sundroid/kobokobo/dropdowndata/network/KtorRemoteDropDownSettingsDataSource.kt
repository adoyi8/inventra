package com.sundroid.kobokobo.dropdowndata.network


import com.sundroid.kobokobo.core.data.safeCall
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util.SETTINGS_SERVICE_URL
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.CityResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.CountryResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationTypeResponse
import com.sundroid.kobokobo.dropdowndata.data.dto.State
import com.sundroid.kobokobo.dropdowndata.data.dto.StateResponse
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
                urlString = "${SETTINGS_SERVICE_URL}/get-country"
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
                urlString = "${SETTINGS_SERVICE_URL}/get-state"
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
                urlString = "${SETTINGS_SERVICE_URL}/get-city"
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
                urlString = "${SETTINGS_SERVICE_URL}/get-organization-type"
            ){
                contentType(ContentType.Application.Json)
                setBody(request)

            }
        }
    }
}