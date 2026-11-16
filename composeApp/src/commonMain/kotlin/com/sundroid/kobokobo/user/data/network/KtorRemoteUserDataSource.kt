package com.sundroid.kobokobo.user.data.network

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.data.safeCall
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util.BASE_URL
import com.sundroid.kobokobo.dashboard.utils.Util.USER_SERVICE_URL
import com.sundroid.kobokobo.dashboard.utils.Util.accessToken
import com.sundroid.kobokobo.user.data.dto.ChangePasswordRequest
import com.sundroid.kobokobo.user.data.dto.LoginRequest
import com.sundroid.kobokobo.user.data.dto.UserLoginResponseDto
import com.sundroid.kobokobo.user.data.dto.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class KtorRemoteUserDataSource(
    private val httpClient: HttpClient
): RemoteUserDataSource {

    override suspend fun searchUser(
        query: String,
        resultLimit: Int?
    ): Result<UserResponseDto, DataError.Remote> {
        return safeCall<UserResponseDto> {
            httpClient.get(
                urlString = "$USER_SERVICE_URL/search.json"
            ) {
                parameter("q", query)
                parameter("limit", resultLimit)
                parameter("language", "eng")
                parameter("fields", "key,title,author_name,author_key,cover_edition_key,cover_i,ratings_average,ratings_count,first_publish_year,language,number_of_pages_median,edition_count")
            }
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun login(username: String, password: String): Result<UserLoginResponseDto, DataError.Remote> {

        val loginBody =LoginRequest(emailAddress = username, password=Base64.encode(password.toByteArray()),ipAddress ="081343902049", deviceUsedToLogin = "ssds", countryOfLogin = "Nigeria", platformId = "1223")

        val response = safeCall<UserLoginResponseDto> {
            httpClient.post(
                urlString = "${USER_SERVICE_URL}/employee-login"
            ){
                contentType(ContentType.Application.Json)
                setBody(loginBody)
            }
        }
       return response
        }
    override suspend fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<ResponseDto, DataError.Remote> {
        val response = safeCall<ResponseDto> {
            httpClient.post(
                urlString = "${BASE_URL}/reset_password/"
            ){
                contentType(ContentType.Application.Json)
                setBody(ChangePasswordRequest(oldPassword, newPassword, confirmPassword))
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

        return response
    }

}