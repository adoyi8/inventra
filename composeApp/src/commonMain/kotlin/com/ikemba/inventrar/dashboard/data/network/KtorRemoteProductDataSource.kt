package com.ikemba.inventrar.dashboard.data.network


import com.ikemba.inventrar.core.data.safeCall
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.data.dto.ProductResponseDto
import com.ikemba.inventrar.dashboard.utils.Util.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType





class KtorRemoteProductDataSource(
    private val httpClient: HttpClient
): RemoteProductDataSource {

    override suspend fun getProducts(
     accessToken: String
    ): Result<ProductResponseDto, DataError.Remote> {

        return safeCall<ProductResponseDto> {
            httpClient.get(
                urlString = "${BASE_URL}/products"
            ){
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }

            }
        }
    }
}