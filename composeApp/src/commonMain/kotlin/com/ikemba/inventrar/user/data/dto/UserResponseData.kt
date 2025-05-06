package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserResponseData (

  @SerialName("account" ) var account : Account? = Account(),
  @SerialName("auth"    ) var auth    : Auth?    = Auth()

)