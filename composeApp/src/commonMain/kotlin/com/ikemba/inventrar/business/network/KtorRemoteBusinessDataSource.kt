package com.ikemba.inventrar.business.network


import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.utils.Util.APP_NAME
import com.ikemba.inventrar.dashboard.utils.Util.AUTHORIZATION_TOKEN
import com.ikemba.inventrar.dashboard.utils.Util.BASE_URL
import com.ikemba.inventrar.dashboard.utils.Util.ORGANIZATION_SERVICE_URL
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType





class KtorRemoteBusinessDataSource(
    private val httpClient: HttpClient
): RemoteBusinessDataSource {

    override suspend fun getBusiness(
        accessToken: String, paginationRequest: SearchOrganizationRequest
    ): Result<OrganizationResponse, DataError.Remote> {
        return safeCall<OrganizationResponse> {
            httpClient.post(
                urlString = "${ORGANIZATION_SERVICE_URL}/search-organization"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append("Authentication", AUTHORIZATION_TOKEN)
                    append("appname", APP_NAME)
                }
                setBody(paginationRequest)

            }
        }
    }

    override suspend fun updateBusiness(
        accessToken: String,
        request: CreateBusinessRequest
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun createBusiness(
        accessToken: String,
        request: CreateBusinessRequest
    ): Result<ResponseDto, DataError.Remote> {
        return safeCall<ResponseDto> {
            httpClient.post(
                urlString = "${ORGANIZATION_SERVICE_URL}/create-organization"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append("Authentication", AUTHORIZATION_TOKEN)
                    append("appname", APP_NAME)
                }
                setBody(request)

            }
        }
    }

    override suspend fun deleteBusiness(
        accessToken: String,
        request: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


}