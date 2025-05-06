package com.ikemba.inventrar.heldOrder.data.network


import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.HeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType


private const val BASE_URL = "https://api.ikembatech.com.au/v1"


class KtorRemoteHeldOrderDataSource(
    private val httpClient: HttpClient
): RemoteHeldOrderDataSource {

    override suspend fun getHeldOrders(
        accessToken: String, paginationRequest: PaginationRequest
    ): Result<HeldOrderDto, DataError.Remote> {
        return safeCall<HeldOrderDto> {
            httpClient.post(
                urlString = "${BASE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(paginationRequest)

            }
        }
    }

    override suspend fun getSingleHeldOrders(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote> {
        return safeCall<SingleHeldOrderDto> {
            httpClient.post(
                urlString = "${BASE_URL}/get_single_held_order/"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(voidOrderRequest)

            }
        }
    }

    override suspend fun holdOrder(voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun voidOrder(accessToken: String,voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote> {
        return safeCall<ResponseDto> {
            httpClient.post(
                urlString = "${BASE_URL}/void/"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(voidOrderRequest)

            }
        }
    }
}