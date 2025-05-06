package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserLoginResponseDto(

    @SerialName("response_code") var responseCode: Int? = null,
    @SerialName("response_message") var responseMessage: String? = null,
    @SerialName("data") var userResponseData: UserResponseData? = UserResponseData()

)