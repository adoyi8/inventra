package com.ikemba.inventrar.dropdowndata.presentation

import android.os.Message
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.State
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType

data class SettingsState (
    var countries: List<Country> = emptyList(),
    var states: List<State> = emptyList(),
    var cities: List<City> = emptyList(),
    var organizationTypes:List<OrganizationType> = emptyList(),
    var isLoading: Boolean = false,
    var errorMessage: String = ""

)