package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val emailAddress: String, val password: String, val deviceUsedToLogin : String ="234", val ipAddress: String ="081343902049",  val countryOfLogin: String = "Nigeria",
val platformId:String = "776e54b6-70b5-45a5-a571-e380a52c3d32")