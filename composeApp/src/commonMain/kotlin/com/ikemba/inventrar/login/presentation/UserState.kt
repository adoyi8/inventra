package com.ikemba.inventrar.login.presentation

import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.user.domain.User

data class UserState (
    val firstName: String ="",
    val lastName: String = "",
    val business: List<SearchOrganizationResult> = emptyList()

)