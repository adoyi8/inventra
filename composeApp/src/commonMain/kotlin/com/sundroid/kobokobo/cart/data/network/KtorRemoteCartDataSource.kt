package com.sundroid.kobokobo.cart.data.network

import com.sundroid.kobokobo.cart.data.dto.PostSalesRequest
import com.sundroid.kobokobo.cart.data.dto.ReceiptResponseDto
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.data.safeCall
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util.BASE_URL
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json



class KtorRemoteCartDataSource(
    private val httpClient: HttpClient
): RemoteCartDataSource {


    override suspend fun postSales(accessToken: String, postSalesRequest: PostSalesRequest): Result<ResponseDto, DataError.Remote> {

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