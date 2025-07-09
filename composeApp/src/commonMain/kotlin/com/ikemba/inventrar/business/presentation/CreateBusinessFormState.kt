package com.ikemba.inventrar.business.presentation

import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.State
data class CreateBusinessFormState (
    val businessName: String = "",
    var businessType: OrganizationType? = null,
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val address: String = "",
    val description: String = "",
    )