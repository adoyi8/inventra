package com.sundroid.kobokobo.transactionHistory.data.network


import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.data.safeCall
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util.BASE_URL
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionHistoryDto
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType





class KtorRemoteTransactionHistoryDataSource(
    private val httpClient: HttpClient
): RemoteTransactionHistoryDataSource {

    override suspend fun getTransactionHistory(
        accessToken: String, paginationRequest: PaginationRequest
    ): Result<TransactionHistoryDto, DataError.Remote> {
        return safeCall<TransactionHistoryDto> {
            httpClient.post(
                urlString = "${BASE_URL}/order_list/"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(paginationRequest)

            }
        }
    }
    override suspend fun getHeldOrders(
        accessToken: String
    ): Result<TransactionHistoryDto, DataError.Remote> {
        return safeCall<TransactionHistoryDto> {
            httpClient.post(
                urlString = "${BASE_URL}/held_order_list/"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(PaginationRequest())

            }
        }
    }

    override suspend fun holdOrder(voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }
}