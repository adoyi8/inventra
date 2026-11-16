package com.sundroid.kobokobo.inventory.presentation

import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.State
data class CreateBusinessFormState (
    val businessName: String = "",
    var businessType: OrganizationType? = null,
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val address: String = "",
    val description: String = "",
    )