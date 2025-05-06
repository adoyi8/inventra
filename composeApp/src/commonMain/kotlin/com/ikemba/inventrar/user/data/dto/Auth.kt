package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Auth(

    @SerialName("access_token") var accessToken: String? = null

)