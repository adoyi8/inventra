package com.sundroid.kobokobo.dropdowndata.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.core.domain.onError
import com.sundroid.kobokobo.core.domain.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sundroid.kobokobo.dropdowndata.data.domain.DropDownSettingsRepository
import com.sundroid.kobokobo.dropdowndata.data.dto.City
import com.sundroid.kobokobo.dropdowndata.data.dto.Country
import com.sundroid.kobokobo.dropdowndata.data.dto.OrganizationType
import com.sundroid.kobokobo.dropdowndata.data.dto.State


class DropDownSettingsViewModel(
    private val settingsRepository: DropDownSettingsRepository,
): ViewModel(){
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    fun getCountries(country: Country) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            settingsRepository.getCountry(country)
                .onSuccess { countryResponse ->

                    if(countryResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, countries = countryResponse.countries!!)
                        }

                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = countryResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }
    fun getState(state: State) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            settingsRepository.getState(state)
                .onSuccess { stateResponse ->

                    if(stateResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, states = stateResponse.states!!)
                        }

                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = stateResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }
    fun getCity(city: City) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            settingsRepository.getCity(city)
                .onSuccess { cityResponse ->

                    if(cityResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, cities = cityResponse.cities)
                        }

                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = cityResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }

    fun getOrganizationType(organizationType: OrganizationType) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            settingsRepository.getOrganizationType(organizationType)
                .onSuccess { organizationResponse ->

                    if(organizationResponse.responseCode == 0){
                        organizationResponse.organizationTypes?.let {
                            _state.update {
                                it.copy( organizationTypes = organizationResponse.organizationTypes)
                            }
                        }
                        _state.update {
                            it.copy( isLoading = false)
                        }

                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = organizationResponse?.responseMessage.toString(), isLoading = false)
                        }
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    hideErrorMessageAfterDelay()
                }
        }
    }

    fun hideErrorMessageAfterDelay(){
        viewModelScope.launch {
            delay(4000)
            _state.update {
                it.copy(errorMessage = "")
            }
        }
    }
}