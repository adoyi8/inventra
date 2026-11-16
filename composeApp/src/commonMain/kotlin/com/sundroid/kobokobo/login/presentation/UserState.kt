package com.sundroid.kobokobo.login.presentation

import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.user.domain.User

data class UserState (
    val firstName: String ="",
    val lastName: String = "",
    val business: List<SearchOrganizationResult> = emptyList()

)