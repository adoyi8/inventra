package com.ikemba.inventrar.inventory.presentation

import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.dropdowndata.data.dto.Country
data class BusinessState (
    val isLoading: Boolean = false,
    var isVisible  : Boolean = false,
    val errorMessage: String = "",
    var showConfirmLogout: Boolean = false,
    var business: List<SearchOrganizationResult> = emptyList(),

)