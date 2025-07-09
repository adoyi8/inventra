package com.ikemba.inventrar.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.domain.onError
import com.ikemba.inventrar.core.domain.onSuccess
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.user.data.mappers.toUser
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.user.data.mappers.toUserEntity
import com.ikemba.inventrar.user.domain.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn


class UserViewModel(
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _state = MutableStateFlow(LoginState())

    private val _userState = MutableStateFlow(UserState())
     var googleAuthUIProvider : GoogleAuthUiProvider? = null
    private var observeUsers: Job? = null

    val state = _state.asStateFlow()
        .onStart {
            observeUsers()

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )
    val userState = _userState.asStateFlow()
    private fun login() {
        viewModelScope.launch {
            userRepository.login(state.value.userName.trim(), state.value.password.trim())
                .onSuccess { userResponse ->

                    if(userResponse?.responseCode == 0){
                        Util.accessToken = userResponse.employee?.token!!
                        userRepository.deleteAllUsers()
                        userRepository.saveUserInLocalDb(userResponse.toUser())
                        _state.update {
                            it.copy( isLoading = false, userName = "", password = "")
                        }
                        NavigationViewModel.navController?.navigate(Route.UserProfileRoute)
                    }
                    else{
                        _state.update {
                            it.copy(errorMessage = userResponse?.responseMessage.toString(), isLoading = false)
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
     fun getBusinesses(searchOrganization: SearchOrganizationRequest) {
        viewModelScope.launch {
            businessRepository.getBusinesses(Util.accessToken, searchOrganization)
                .onSuccess { businessResponse ->

                    if(businessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, password = "")
                        }
                        businessResponse.organizations?.let { business->
                            _userState.update {
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
            businessRepository.createBusiness(Util.accessToken, createBusinessRequest)
                .onSuccess { createBusinessResponse ->

                    if(createBusinessResponse.responseCode == 0){
                        _state.update {
                            it.copy( isLoading = false, password = "")
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

    private fun observeUsers() {
        observeUsers?.cancel()
        observeUsers = userRepository
            .getAllUsers()
            .onEach { users ->
                if (users.isNotEmpty()) {
                    _state.update { it ->
                        it.copy(
                            currentUser = users.first()
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    fun getUser(): User? {
        println("ohimi "+ _state.value.currentUser!!.organizationId)
        return (_state.value.currentUser)
    }
    fun logout() {
        viewModelScope.launch {
            hideShowConfirmLogout()
            userRepository.deleteAllUsers()
            googleAuthUIProvider?.signOut()
            NavigationViewModel.navController?.navigate(Route.Login)
        }
    }
    fun hideShowConfirmLogout() {
        _state.update {
            it.copy(showConfirmLogout = false)
        }
    }
    fun showShowConfirmLogout() {
        viewModelScope.launch {
            _state.update {
                it.copy(showConfirmLogout = true)
            }
        }
    }
    fun onAction(action: LoginAction){
        when(action){
            is LoginAction.OnLoginButtonClicked ->{
                _state.update {
                    it.copy(errorMessage = "")
                }
                if(state.value.userName.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your username")
                    }
                    hideErrorMessageAfterDelay()
                    return
                }
                if(state.value.password.isEmpty()){
                    _state.update {
                        it.copy(errorMessage = "Please enter your password")
                    }
                    hideErrorMessageAfterDelay()
                    return
                }
                _state.update {
                    it.copy(isLoading = true)
                }

               login()

            }
            is LoginAction.OnPasswordChange ->{
                _state.update {
                    it.copy(password = action.password)
                }
            }
            is LoginAction.OnUserNameChange ->{

                _state.update {
                    it.copy(userName = action.userName)
                }
            }
            is LoginAction.OnToggleVisibility ->{

                _state.update {
                    it.copy(isVisible =!it.isVisible)
                }
            }
            else->{

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

    fun setSelectedBusiness(result: SearchOrganizationResult) {
        viewModelScope.launch {
            val user = getUser()!!
            user.organizationId = result.organizationId
            user.organizationName = result.organizationName
            userRepository.saveUserInLocalDb(user)
        }

    }
}