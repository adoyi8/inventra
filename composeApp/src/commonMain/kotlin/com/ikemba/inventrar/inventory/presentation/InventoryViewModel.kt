package com.ikemba.inventrar.inventory.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.presentation.BusinessState
import com.ikemba.inventrar.business.presentation.CreateBusinessFormState
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.dropdowndata.data.domain.DropDownSettingsRepository
import com.ikemba.inventrar.dropdowndata.data.dto.City
import com.ikemba.inventrar.dropdowndata.data.dto.Country
import com.ikemba.inventrar.dropdowndata.data.dto.OrganizationType
import com.ikemba.inventrar.dropdowndata.data.dto.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val settingsRepository: DropDownSettingsRepository,
    private val businessRepository: BusinessRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow(BusinessState())
    private val _createBusinessFormState = MutableStateFlow(CreateBusinessFormState())

     var googleAuthUIProvider : GoogleAuthUiProvider? = null

    val state = _state.asStateFlow()

    val createBusinessFormState = _createBusinessFormState.asStateFlow()


     fun getBusinesses(searchOrganization: SearchOrganizationRequest) {
        viewModelScope.launch {
            businessRepository.getBusinesses(Util.accessToken, searchOrganization)
                .onSuccess { businessResponse ->

                    if(businessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false)
                        }
                        businessResponse.organizations?.let { business->
                            _state.update {
                                it.copy(business = business)
                            }
                        }
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = businessResponse?.responseMessage.toString(), isLoading = false)
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
    private fun createBusinesses(createBusinessRequest: CreateBusinessRequest) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            businessRepository.createBusiness(Util.accessToken, createBusinessRequest)
                .onSuccess { createBusinessResponse ->

                    if(createBusinessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false)
                        }
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = createBusinessResponse?.responseMessage.toString(), isLoading = false)
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

    fun updateSelectedCountry(country: Country) {
        _createBusinessFormState.update {
            it.copy(selectedCountry = country)
        }
        hideErrorMessageAfterDelay()

    }
    fun updateSelectedState(state: State) {
        _createBusinessFormState.update {
            it.copy(selectedState = state)
        }
        hideErrorMessageAfterDelay()
    }
    fun updateSelectedCity(city: City) {
        _createBusinessFormState.update {
            it.copy(selectedCity = city)
        }
        hideErrorMessageAfterDelay()

    }

    fun updateSelectedBusinessType(type: OrganizationType) {
        _createBusinessFormState.update {
            it.copy(businessType = type)
        }
        hideErrorMessageAfterDelay()
    }

    fun validateThenCreateBusiness(createdBy: String){
        viewModelScope.launch {
            if(_createBusinessFormState.value.businessName.isNullOrEmpty()){
                _state.update {
                    it.copy(errorMessage = "Business name is required")
                }
                hideErrorMessageAfterDelay()
                return@launch
            }
            else if(_createBusinessFormState.value.businessType == null){
                _state.update {
                    it.copy(errorMessage = "Business type is required")
                }
            }
            else if(_createBusinessFormState.value.selectedCountry == null){
                _state.update {
                    it.copy(errorMessage = "Country is required")
                }
            }
            else if(_createBusinessFormState.value.selectedState == null){
                _state.update {
                    it.copy(errorMessage = "State is required")
                }
            }
            else if(_createBusinessFormState.value.selectedCity == null){
                _state.update {
                    it.copy(errorMessage = "City is required")
                }
            }
            else{
                val createBusinessRequest = CreateBusinessRequest(
                    organizationName = _createBusinessFormState.value.businessName,
                    organizationTypeId = _createBusinessFormState.value.businessType!!.organizationTypeId,
                    description = _createBusinessFormState.value.description,
                    stateId = _createBusinessFormState.value.selectedState!!.stateId,
                    cityId = _createBusinessFormState.value.selectedCity!!.cityId,
                    countryId = _createBusinessFormState.value.selectedCountry!!.countryId,
                    address = _createBusinessFormState.value.address,
                    createdBy = createdBy
                )
                createBusinesses(createBusinessRequest)
            }
        }
    }

    fun updateBusinessAddress(string: String) {
        _createBusinessFormState.update {
            it.copy(address = string)
        }
        hideErrorMessageAfterDelay()
    }

    fun updateBusinessDescription(string: String) {
        _createBusinessFormState.update {
            it.copy(description = string)
        }
        hideErrorMessageAfterDelay()

    }

    fun updateBusinessName(string: String) {
        _createBusinessFormState.update {
            it.copy(businessName = string)
        }
        hideErrorMessageAfterDelay()

    }
}