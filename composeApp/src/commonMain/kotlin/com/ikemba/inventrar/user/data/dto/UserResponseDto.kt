package com.ikemba.inventrar.user.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    @SerialName("docs") val results: List<UserDto>
)
