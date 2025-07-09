package com.ikemba.inventrar.dropdowndata.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.ikemba.inventrar.dropdowndata.data.domain.DropDownSettingsRepository
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.State


class DropDownSettingsViewModel(
    private val settingsRepository: DropDownSettingsRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    fun getCountries(country: Country) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            println("tesla 3")
            settingsRepository.getCountry(country)
                .onSuccess { countryResponse ->

                    if(countryResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, countries = countryResponse.countries!!)
                        }

                        println("tesla 1")
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = countryResponse?.responseMessage.toString(), isLoading = false)
                        }
                        println("tesla 2 "+ countryResponse?.responseMessage.toString())
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    println("tesla 4 "+ error.toString())
                    hideErrorMessageAfterDelay()
                }
        }
    }
    fun getState(state: State) {
        viewModelScope.launch {
            println("tesla 3")
            _state.update {
                it.copy(isLoading = true)
            }
            settingsRepository.getState(state)
                .onSuccess { stateResponse ->

                    if(stateResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, states = stateResponse.states!!)
                        }

                        println("tesla 1")
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = stateResponse?.responseMessage.toString(), isLoading = false)
                        }
                        println("tesla 2 "+ stateResponse?.responseMessage.toString())
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    println("tesla 4 "+ error.toString())
                    hideErrorMessageAfterDelay()
                }
        }
    }
    fun getCity(city: City) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            println("tesla 3")
            settingsRepository.getCity(city)
                .onSuccess { cityResponse ->

                    if(cityResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, cities = cityResponse.cities)
                        }

                        println("tesla 1")
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = cityResponse?.responseMessage.toString(), isLoading = false)
                        }
                        println("tesla 2 "+ cityResponse?.responseMessage.toString())
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    println("tesla 4 "+ error.toString())
                    hideErrorMessageAfterDelay()
                }
        }
    }

    fun getOrganizationType(organizationType: OrganizationType) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            println("tesla 3")
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

                        println("tesla 1")
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = organizationResponse?.responseMessage.toString(), isLoading = false)
                        }
                        println("tesla 2 "+ organizationResponse?.responseMessage.toString())
                        hideErrorMessageAfterDelay()
                    }

                }
                .onError { error->
                    _state.update {
                        it.copy(errorMessage = error.toString(), isLoading = false)
                    }
                    println("tesla 4 "+ error.toString())
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