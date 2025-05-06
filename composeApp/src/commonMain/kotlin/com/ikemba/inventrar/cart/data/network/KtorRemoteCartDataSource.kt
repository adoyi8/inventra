package com.ikemba.inventrar.cart.data.network

import com.ikemba.inventrar.cart.data.dto.PostSalesRequest
import com.ikemba.inventrar.cart.data.dto.ReceiptResponseDto
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


private const val BASE_URL = "https://api.ikembatech.com.au/v1"
class KtorRemoteCartDataSource(
    private val httpClient: HttpClient
): RemoteCartDataSource {


    override suspend fun postSales(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto, DataError.Remote> {
        println("eze "+ Json.encodeToString(postSalesRequest))
        return safeCall<ResponseDto> {
            httpClient.post(
                urlString = "$BASE_URL/post_sales"
            ) {
                contentType(ContentType.Application.Json)

                setBody(postSalesRequest)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }

            }
        }
    }
    override suspend fun holdOrder(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto, DataError.Remote> {
        println("eze "+ Json.encodeToString(postSalesRequest))
        return safeCall<ResponseDto> {
            httpClient.post(
                urlString = "$BASE_URL/hold"
            ) {
                contentType(ContentType.Application.Json)

                setBody(postSalesRequest)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }

            }
        }
    }

    override suspend fun getOrderReceipt(
        accessToken: String,
        getReceiptRequest: VoidOrderRequest
    ): Result<ReceiptResponseDto, DataError.Remote> {
        return safeCall<ReceiptResponseDto> {
            httpClient.post(
                urlString = "$BASE_URL/get_order_receipt"
            ) {
                contentType(ContentType.Application.Json)

                setBody(getReceiptRequest)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }

            }
        }
    }
}