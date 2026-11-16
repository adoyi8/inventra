package com.sundroid.kobokobo.dropdowndata.presentation

import android.os.Message
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationResult
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.State
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType

data class SettingsState (
    var countries: List<Country> = emptyList(),
    var states: List<State> = emptyList(),
    var cities: List<City> = emptyList(),
    var organizationTypes:List<OrganizationType> = emptyList(),
    var isLoading: Boolean = false,
    var errorMessage: String = ""

)