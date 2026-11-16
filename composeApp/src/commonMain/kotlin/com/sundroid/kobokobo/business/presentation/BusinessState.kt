package com.sundroid.kobokobo.business.presentation

import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
data class BusinessState (
    val isLoading: Boolean = false,
    var isVisible  : Boolean = false,
    val errorMessage: String = "",
    var showConfirmLogout: Boolean = false,
    var business: List<SearchOrganizationResult> = emptyList(),

)