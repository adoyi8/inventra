package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val emailAddress: String, val password: String, val deviceUsedToLogin : String ="234", val ipAddress: String,  val countryOfLogin: String,
val platformId:String)