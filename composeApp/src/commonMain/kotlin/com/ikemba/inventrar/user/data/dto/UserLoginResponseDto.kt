package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserLoginResponseDto(

    @SerialName("responseCode") var responseCode: Int? = null,
    @SerialName("responseMessage") var responseMessage: String? = null,
    @SerialName("data") var userResponseData: UserResponseData? = UserResponseData()

)