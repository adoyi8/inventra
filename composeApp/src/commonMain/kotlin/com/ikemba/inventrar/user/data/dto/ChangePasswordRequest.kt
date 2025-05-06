package com.ikemba.inventrar.user.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(val current_password: String, val new_password: String, val confirm_password: String)